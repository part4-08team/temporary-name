package project.closet.user.service.basic;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dto.request.UserCreateRequest;
import project.closet.dto.response.UserDto;
import project.closet.exception.user.UserAlreadyExistsException;
import project.closet.user.entity.Profile;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;
import project.closet.user.service.UserService;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public UserDto create(UserCreateRequest userCreateRequest) {
        log.debug("사용자 생성 시작: {}", userCreateRequest);

        String name = userCreateRequest.name();
        String email = userCreateRequest.email();

        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException.withEmail(email);
        }
        if (userRepository.existsByName(name)) {
            throw UserAlreadyExistsException.withName(name);
        }

        // User Entity 생성
        String hashedPassword = passwordEncoder.encode(userCreateRequest.password());
        User user = new User(name, email, hashedPassword);
        // Profile Entity 생성
        Profile.createDefault(user);
        // User Entity 저장
        userRepository.save(user);
        return UserDto.from(user);
    }
}
