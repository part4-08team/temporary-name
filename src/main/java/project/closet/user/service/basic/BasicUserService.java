package project.closet.user.service.basic;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import project.closet.dto.request.ProfileUpdateRequest;
import project.closet.dto.request.UserCreateRequest;
import project.closet.dto.response.ProfileDto;
import project.closet.dto.response.UserDto;
import project.closet.dto.response.WeatherAPILocation;
import project.closet.exception.user.UserAlreadyExistsException;
import project.closet.exception.user.UserNotFoundException;
import project.closet.user.entity.Profile;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;
import project.closet.user.service.UserService;
import project.closet.weather.service.basic.GeoGridConverter;
import project.closet.weather.service.basic.GeoGridConverter.Grid;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeoGridConverter geoGridConverter;

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

        String hashedPassword = passwordEncoder.encode(userCreateRequest.password());
        User user = new User(name, email, hashedPassword);
        Profile.createDefault(user);
        userRepository.save(user);
        return UserDto.from(user);
    }

    @Transactional(readOnly = true)
    @Override
    public ProfileDto getProfile(UUID userId) {
        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        // User -> ProfileDto 변환
        return toProfileDto(user);
    }

    @PreAuthorize("principal.userId == #userId")
    @Transactional
    @Override
    public ProfileDto updateProfile(
            UUID userId,
            ProfileUpdateRequest profileUpdateRequest,
            MultipartFile profileImage
    ) {
        log.debug("사용자 프로필 업데이트 시작: userId={}, request={}", userId, profileUpdateRequest);
        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        user.updateProfile(profileUpdateRequest);
        return toProfileDto(user);
    }

    private ProfileDto toProfileDto(User user) {
        Profile profile = user.getProfile();
        WeatherAPILocation location = null;

        if (profile.getLatitude() != null && profile.getLongitude() != null) {
            Grid grid = geoGridConverter.convert(profile.getLatitude(), profile.getLongitude());
            location = new WeatherAPILocation(
                    profile.getLatitude(),
                    profile.getLongitude(),
                    grid.x(),
                    grid.y(),
                    profile.getLocationNames()
            );
        }

        return ProfileDto.of(user, location, profile);
    }
}
