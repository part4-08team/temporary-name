package project.closet.auth.service.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.auth.service.AuthService;
import project.closet.dto.response.UserDto;
import project.closet.exception.user.UserNotFoundException;
import project.closet.mail.MailService;
import project.closet.security.jwt.JwtService;
import project.closet.user.entity.Profile;
import project.closet.user.entity.Role;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    private static final char[] PASSWORD_CHARS = (
            "1234567890" +
                    "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                    "abcdefghijklmnopqrstuvwxyz" +
                    "!@#$%^&*()"
    ).toCharArray();
    private static final int TEMP_PASSWORD_LENGTH = 10;

    @Value("${closet.admin.username}")
    private String username;
    @Value("${closet.admin.email}")
    private String email;
    @Value("${closet.admin.password}")
    private String password;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;

    @Override
    public void initAdmin() {
        if (userRepository.existsByEmail(email) || userRepository.existsByName(username)) {
            log.warn("이미 어드민이 존재합니다.");
            return;
        }

        String encodedPassword = passwordEncoder.encode(password);
        User admin = new User(username, email, encodedPassword);
        Profile.createDefault(admin);
        admin.updateRole(Role.ADMIN);
        userRepository.save(admin);

        UserDto adminDto = UserDto.from(admin);
        log.info("어드민 계정이 생성되었습니다: {}", adminDto);
    }

    @Transactional
    @Override
    public void resetPassword(String email) {
        // 1. User 엔티티 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> UserNotFoundException.withEmail(email));

        // 2-1. 임시 비밀번호 생성
        String tempPassword = generateTempPassword();
        // 2-2. 임시 비밀번호를 암호화
        String encodedTempPassword = passwordEncoder.encode(tempPassword);
        // 3. User password 임시 비밀번호로 초기화
        user.updatePassword(encodedTempPassword);
        // 4. 임시 비밀번호 만료 테이블에 저장

        // 초기화된 임시 비밀번호를 User email 로 발송해주기
        mailService.sendMimeMail(
                email,
                "[Closet] 임시 비밀번호 발급",
                tempPassword
        );
    }

    private String generateTempPassword() {
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = (int) (Math.random() * PASSWORD_CHARS.length);
            password.append(PASSWORD_CHARS[index]);
        }
        return password.toString();
    }

}
