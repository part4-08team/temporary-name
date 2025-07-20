package project.closet.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailServiceIntegrationTest {

    @Autowired
    private MailService mailService;

    @Disabled
    @DisplayName("메일을 실제 발송합니다.")
    @Test
    void sendMail_integration() {
        // 이 테스트를 돌리면 실제로 내 Gmail 수신함에 mail이 도착합니다.
        mailService.sendSimpleMail(
                "", // 본인의 Gmail 주소를 입력하세요.
                "Integration Test",
                "Integration 테스트용 본문입니다."
        );
    }
}
