package project.closet.domain.users.user;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.users.auth.dto.ChangePasswordRequest;
import project.closet.domain.users.user.dto.ProfileDto;
import project.closet.domain.users.user.dto.ProfileFindRequest;
import project.closet.domain.users.user.dto.ProfileUpdateRequest;
import project.closet.domain.users.user.dto.ProfileUpdateWithImageUrlRequest;
import project.closet.domain.users.user.dto.UserCreateRequest;
import project.closet.domain.users.user.dto.UserDto;
import project.closet.domain.users.user.dto.UserLockUpdateRequest;
import project.closet.domain.users.user.dto.UserRoleUpdateRequest;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProfileRepository profileRepository;

  @Transactional(readOnly = true)
  @Override
  public UserDto getUsers(ProfileFindRequest dto) {



    return null;
  }

  @Override
  public UserDto registerUser(UserCreateRequest dto) {

    validateDuplicatedEmail(dto.email());
    String hashedPassword = generateHashedPassword(dto.password());

    User user = User.createUserWithProfile(dto.name(), dto.email(), hashedPassword);
    User savedUser = userRepository.save(user);

    return UserDto.from(savedUser);
  }

  @Override
  public UserDto updateUserRole(UUID userId, UserRoleUpdateRequest request) {

    User user = findUserById(userId);
    user.changeRole(request.role());
    return UserDto.from(user);
  }


  @Transactional(readOnly = true)
  @Override
  public ProfileDto getUserProfile(UUID userId) {

    Profile profile = findProfileByUserId(userId);
    return ProfileDto.of(userId, profile);
  }

  @Override
  public ProfileDto updateUserProfile(UUID userId, ProfileUpdateWithImageUrlRequest request) {

    Profile profile = findProfileByUserId(userId);
    updateProfile(profile, request);
    return ProfileDto.of(userId, profile);
  }

  @Override
  public void updateUserPassword(UUID userId, ChangePasswordRequest request) {

    User user = findUserById(userId);
    user.changePassword(generateHashedPassword(request.password()));
  }

  @Override
  public UUID updateUserLock(UUID userId, UserLockUpdateRequest request) {

    User user = findUserById(userId);
    user.changeLock(request.locked());
    return user.getId();
  }

  private String generateHashedPassword(String password) {
    return passwordEncoder.encode(password);
  }

  private void validateDuplicatedEmail(String email) {
    if (userRepository.existsByEmail(email)){
      throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
    }
  }

  private User findUserById(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
  }

  private Profile findProfileByUserId(UUID userId) {
    return profileRepository.findByUserId(userId)
        .orElseThrow(
            () -> new IllegalArgumentException("프로필 조회 실패 : userId에 해당하는 Profile 이 존재하지 않습니다."));
  }

  private void updateProfile(Profile profile, ProfileUpdateWithImageUrlRequest dto) {
    ProfileUpdateRequest request = dto.profileUpdateRequest();
    profile.changeName(request.name());
    profile.changeLocation(request.location());
    profile.changeBirthDate(request.birthDate());
    profile.changeGender(request.gender());
    profile.changeTemperatureSensitivity(request.temperatureSensitivity());
    profile.changeProfileImageUrl(dto.imageUrl());
  }
}
