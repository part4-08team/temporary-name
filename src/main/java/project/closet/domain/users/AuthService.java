package project.closet.domain.users;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.users.util.TemporaryPasswordFactory;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JavaMailSender javaMailSender;
  private final UserRepository userRepository;

  // 메일 전송
  @Transactional
  public void sendEmail(UUID userId) {
    // email, password
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
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
