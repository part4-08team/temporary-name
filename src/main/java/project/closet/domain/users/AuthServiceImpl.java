package project.closet.domain.users;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.users.dto.ResetPasswordRequest;
import project.closet.domain.users.repository.UserRepository;
import project.closet.domain.users.util.TemporaryPasswordFactory;
import project.closet.global.config.security.JwtUtils;
import project.closet.global.config.security.TokenType;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final JavaMailSender javaMailSender;
  private final UserRepository userRepository;
  private final JwtUtils jwtUtils;

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
