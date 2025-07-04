package project.closet.auth.service.basic;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.auth.service.AuthService;
import project.closet.dto.request.RoleUpdateRequest;
import project.closet.dto.response.UserDto;
import project.closet.exception.user.UserNotFoundException;
import project.closet.security.jwt.JwtService;
import project.closet.user.entity.Profile;
import project.closet.user.entity.Role;
import project.closet.user.entity.User;
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
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    @Override
    public UserDto updateRole(RoleUpdateRequest request) {
        UUID userId = request.userId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        user.updateRole(request.newRole());

        jwtService.invalidateJwtSession(user.getId());
        return UserDto.from(user);
    }
}
