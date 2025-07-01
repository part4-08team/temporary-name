package project.closet.domain.users;

import io.jsonwebtoken.ExpiredJwtException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.users.User.UserRole;
import project.closet.domain.users.dto.ResetPasswordRequest;
import project.closet.domain.users.dto.SignInRequest;
import project.closet.domain.users.dto.SignInResponse;
import project.closet.domain.users.repository.UserRepository;
import project.closet.domain.users.util.TemporaryPasswordFactory;
import project.closet.common.redis.RedisRepository;
import project.closet.global.config.security.AdminProperties;
import project.closet.global.config.security.ClosetUserDetails;
import project.closet.global.config.security.JWTConfigProperties;
import project.closet.global.config.security.JwtBlackList;
import project.closet.global.config.security.JwtUtils;
import project.closet.global.config.security.TokenType;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JavaMailSender javaMailSender;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;
  private final RedisRepository redisRepository;
  private final JWTConfigProperties jwtProperties;
  private final JwtBlackList jwtBlackList;
  private final AdminProperties adminProperties;

  @Transactional
  @Override
  public void initAdmin() {
    if (userRepository.existsByEmail(adminProperties.email())) {
      log.info("Admin email already exists");
      return;
    }

    User user = User.createUserWithProfile(adminProperties.username(),
        adminProperties.email(), adminProperties.password());
    user.changeRole(UserRole.ADMIN);
    userRepository.save(user);
    log.info("Admin created");
  }

  @Override
  public void logout(String accessToken) {
    UUID userId = jwtUtils.getUserId(accessToken);
    redisRepository.deleteByUserId(userId);
  }

  @Override
  public SignInResponse login(SignInRequest request) {

    // 인증
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
        request.email(),
        request.password());
    Authentication authenticate = authenticationManager.authenticate(authentication);

    if (authenticate == null || !authenticate.isAuthenticated()) {
      throw new BadCredentialsException("Failed to authenticate");
    }

    ClosetUserDetails userDetails = (ClosetUserDetails) authenticate.getPrincipal();

    // access, refresh token 발급
    try {
      @SuppressWarnings("unchecked")
      String accessToken = jwtUtils.createJwtToken(
          TokenType.ACCESS,
          userDetails.getUserId(),
          userDetails.getUsername(),
          (List<GrantedAuthority>) authenticate.getAuthorities());

      @SuppressWarnings("unchecked")
      String refreshToken = jwtUtils.createJwtToken(
          TokenType.REFRESH,
          userDetails.getUserId(),
          userDetails.getUsername(),
          (List<GrantedAuthority>) authenticate.getAuthorities());

      redisRepository.save(userDetails.getUserId(), accessToken,
          Duration.ofSeconds(jwtProperties.refreshExpiration()));

      return new SignInResponse(accessToken, refreshToken);
    } catch (Exception e) {
      throw new BadCredentialsException("Failed to create token");
    }
  }


  // todo : recilence 로직 구현 : 설정 값보다 더 실패하면 관리자한테 메일 보내기 (send 부분만 recilence)
  @Transactional
  @Override
  public void resetPassword(ResetPasswordRequest request) {

    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new IllegalArgumentException("Wrong email : No User matches"));

    String tempPassword = TemporaryPasswordFactory.createTempPassword();
    user.changePassword(tempPassword);

    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setTo(user.getEmail());
    simpleMailMessage.setSubject("[임시 비밀번호 발급]");
    String message = "임시 비밀번호 발급 메일입니다. \n [임시 비밀번호] : " + tempPassword;
    simpleMailMessage.setText(message);

    try {
      javaMailSender.send(simpleMailMessage);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }


  /**
   * 검증 : 타입 + 만료시간
   * todo : Refresh Rotate 방식으로 할건지 확인
   */
  @Override
  public String reissueAccessToken(String refreshToken) {

    validateRefreshTokenType(refreshToken);
    validateRefreshTokenExpiration(refreshToken);

    UUID userId = jwtUtils.getUserId(refreshToken);
    if (!redisRepository.existsByUserId(userId)) {
      throw new BadCredentialsException("Reissue Error : Invalid refresh token");
    }

    if (jwtBlackList.isBlackListed(userId)) {
      throw new BadCredentialsException("You are Black");
    }

    try {
      String newAccessToken = jwtUtils.createJwtToken(
          TokenType.ACCESS,
          userId,
          jwtUtils.getUsername(refreshToken),
          jwtUtils.getAuthorities(refreshToken));

      redisRepository.save(userId, newAccessToken, Duration.ofSeconds(jwtUtils.getTtl(refreshToken)));
      return newAccessToken;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getAccessToken(String refreshToken) {
    UUID userId = jwtUtils.getUserId(refreshToken);
    return (String) redisRepository.findByUserId(userId);
  }

  private void validateRefreshTokenType(String token) {
    String tokenType = jwtUtils.getTokenType(token);
    if (!TokenType.REFRESH.name().equals(tokenType)) {
      throw new IllegalArgumentException("Invalid token type");
    }
  }

  private void validateRefreshTokenExpiration(String refreshToken) {
    try {
      jwtUtils.validateTokenExpiration(refreshToken);
    } catch (ExpiredJwtException e) {
      // 예외 Example value 형식으로
      throw new IllegalArgumentException("Refresh token is expired");
    }
  }

}
