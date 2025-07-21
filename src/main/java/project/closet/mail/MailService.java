package project.closet.mail;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailService {

    public static final String STATIC_PASSWORD_RESET_HTML = "static/password-reset.html";

    private final JavaMailSender mailSender;

    public void sendSimpleMail(
            String to,
            String subject,
            String text
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        log.info("Sending email to: {}, subject: {}", to, subject);
        mailSender.send(message);
    }

    public void sendMimeMail(
            String to,
            String subject,
            String text
    ) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);

            ClassPathResource resource = new ClassPathResource(STATIC_PASSWORD_RESET_HTML);
            String html = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
            html = html.replace("${tempPassword}", text);

            mimeMessageHelper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Error while sending MIME email", e);
            throw new RuntimeException("Failed to send MIME email", e);
        }
    }

}
