package project.closet.domain.users;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.users.dto.ResetPasswordRequest;
import project.closet.domain.users.dto.SignInRequest;
import project.closet.domain.users.dto.SignInResponse;
import project.closet.domain.users.repository.UserRepository;
import project.closet.domain.users.util.TemporaryPasswordFactory;
import project.closet.global.config.security.ClosetUserDetails;
import project.closet.global.config.security.JwtUtils;
import project.closet.global.config.security.TokenType;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JavaMailSender javaMailSender;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;
  private final AuthenticationManager authenticationManager;


  @Override
  public SignInResponse login(SignInRequest request) {

    // 인증
    Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(
        request.email(),
        request.password());
    Authentication authenticate = authenticationManager.authenticate(authentication);

    if (authenticate == null || !authenticate.isAuthenticated()) {
      throw new BadCredentialsException("Bad credentials");
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

      return new SignInResponse(accessToken, refreshToken);
    } catch (Exception e) {
      throw new BadCredentialsException("Authorization is failed");
    }
  }


  // todo : recilence 로직 구현 : 설정 값보다 더 실패하면 관리자한테 메일 보내기
  @Transactional
  @Override
  public void resetPassword(ResetPasswordRequest request) {

    User user = userRepository.findByEmail(request.email())
        .orElseThrow(
            () -> new IllegalArgumentException("Wrong email : No User matches this email."));

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
   * todo : 추가로 검증할 것 : BlackList
   * todo : Refresh Rotate 방식으로 할건지 확인
   */
  @Override
  public String reissueAccessToken(String refreshToken) {
    // 토큰 타입 확인
    validateRefreshTokenType(refreshToken);
    // refresh token 만료 시간 검증
    try {
      jwtUtils.validateTokenExpiration(refreshToken);
    } catch (ExpiredJwtException e) {
      // 예외 Example value 형식으로
      throw new IllegalArgumentException("Refresh token is expired");
    }

    try {
      return jwtUtils.createJwtToken(
          TokenType.ACCESS,
          jwtUtils.getUserId(refreshToken),
          jwtUtils.getUsername(refreshToken),
          jwtUtils.getAuthorities(refreshToken));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private void validateRefreshTokenType(String token) {
    String tokenType = jwtUtils.getTokenType(token);
    if (!TokenType.REFRESH.name().equals(tokenType)) {
      throw new IllegalArgumentException("Invalid token type");
    }
  }
}
