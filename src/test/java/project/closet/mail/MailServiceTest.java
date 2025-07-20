package project.closet.mail;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import javax.mail.Address;
import org.junit.jupiter.api.*;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class MailServiceTest {

    private Wiser wiser;
    private MailService mailService;

    @BeforeEach
    void setUp() {
        // 2500번 포트에 로컬 SMTP 서버 띄우기
        wiser = new Wiser();
        wiser.setHostname("localhost");
        wiser.setPort(2500);
        wiser.start();

        // JavaMailSenderImpl를 Wiser로 포인팅
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost("localhost");
        sender.setPort(2500);
        mailService = new MailService(sender);
    }

    @AfterEach
    void tearDown() {
        wiser.stop();
    }

    @Test
    void sendSimpleMail_shouldDeliverMessage() throws Exception {
        // when
        mailService.sendSimpleMail(
                "recipient@example.com",
                "Test Subject",
                "Hello, Wiser!"
        );

        // then
        assertEquals(1, wiser.getMessages().size());
        WiserMessage wm = wiser.getMessages().get(0);
        MimeMessage msg = wm.getMimeMessage();

        // (1) 헤더 검증
        assertEquals("Test Subject", msg.getSubject());
        Address[] froms = msg.getFrom();
        assertEquals("noreply@closet.com", ((InternetAddress) froms[0]).getAddress());
        Address[] tos = msg.getRecipients(Message.RecipientType.TO);
        assertEquals("recipient@example.com", ((InternetAddress) tos[0]).getAddress());

        // (2) 바디 텍스트는 writeTo로 덤프한 원시 MIME에서 확인
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        msg.writeTo(baos);
        String raw = baos.toString(StandardCharsets.UTF_8);
        assertTrue(raw.contains("Hello, Wiser!"));
    }
}
