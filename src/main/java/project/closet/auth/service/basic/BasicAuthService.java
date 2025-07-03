package project.closet.auth.service.basic;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.auth.service.AuthService;
import project.closet.dto.request.RoleUpdateRequest;
import project.closet.dto.response.UserDto;
import project.closet.exception.user.UserNotFoundException;
import project.closet.security.ClosetUserDetails;
import project.closet.user.entity.Role;
import project.closet.user.entity.User;
import project.closet.user.mapper.UserMapper;
import project.closet.user.repository.UserRepository;

@Slf4j
@RequiredArgsConstructor
@Service
public class BasicAuthService implements AuthService {

    @Value("${closet.admin.username}")
    private String username;
    @Value("${closet.admin.email}")
    private String email;
    @Value("${closet.admin.password}")
    private String password;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    @Override
    public void initAdmin() {
        if (userRepository.existsByEmail(email) || userRepository.existsByUsername(username)) {
            log.warn("이미 어드민이 존재합니다.");
            return;
        }

        String encodedPassword = passwordEncoder.encode(password);
        User admin = new User(username, email, encodedPassword);
        admin.updateRole(Role.ADMIN);
        userRepository.save(admin);

        UserDto adminDto = userMapper.toDto(admin);
        log.info("어드민 계정이 생성되었습니다: {}", adminDto);
    }

    @Transactional
    @Override
    public UserDto updateRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        user.updateRole(request.newRole());

        // 세션 만료 처리
        sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> ((ClosetUserDetails) principal).getUserDto().id().equals(userId))
                .findFirst()
                .ifPresent(principal -> {
                    List<SessionInformation> activeSessions =
                            sessionRegistry.getAllSessions(principal, false);
                    log.debug("Active sessions: {}", activeSessions.size());
                    activeSessions.forEach(SessionInformation::expireNow);
                });
        return userMapper.toDto(user);
    }
}
