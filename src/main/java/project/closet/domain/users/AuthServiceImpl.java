package project.closet.domain.users;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.users.dto.ResetPasswordRequest;
import project.closet.domain.users.repository.UserRepository;
import project.closet.domain.users.util.TemporaryPasswordFactory;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

  private final JavaMailSender javaMailSender;
  private final UserRepository userRepository;

  // todo : recilence 로직 구현 : 설정 값보다 더 실패하면 관리자한테 메일 보내기
  @Transactional
  @Override
  public void resetPassword(ResetPasswordRequest request) {

    User user = userRepository.findByEmail(request.email())
        .orElseThrow(() -> new IllegalArgumentException("Wrong email : No User matches this email."));

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
}
