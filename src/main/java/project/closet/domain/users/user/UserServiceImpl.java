package project.closet.domain.users.user;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.users.auth.dto.ChangePasswordRequest;
import project.closet.domain.users.user.dto.ProfileDto;
import project.closet.domain.users.user.dto.ProfileFindRequest;
import project.closet.domain.users.user.dto.ProfileUpdateWithImageUrlRequest;
import project.closet.domain.users.user.dto.UserCreateRequest;
import project.closet.domain.users.user.dto.UserDto;
import project.closet.domain.users.user.dto.UserLockUpdateRequest;
import project.closet.domain.users.user.dto.UserRoleUpdateRequest;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ProfileRepository profileRepository;

  @Override
  public UserDto getUsers(ProfileFindRequest dto) {


    return null;
  }

  @Transactional
  @Override
  public UserDto registerUser(UserCreateRequest dto) {

    validateDuplicatedEmail(dto.email());
    String hashedPassword = generateHashedPassword(dto.password());

    User user = User.createUserWithProfile(dto.name(), dto.email(), hashedPassword);
    User savedUser = userRepository.save(user);

    return UserDto.from(savedUser);
  }

  @Transactional
  @Override
  public UserDto updateUserRole(UUID userId, UserRoleUpdateRequest request) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

    user.updateRole(UserRole.ADMIN);
    return UserDto.from(user);
  }

  @Override
  public ProfileDto getUserProfile(UUID userId) {
    return null;
  }

  @Override
  public ProfileDto updateUserProfile(UUID userId, ProfileUpdateWithImageUrlRequest request) {

    Profile profile = profileRepository.findByUserId(userId)
        .orElseThrow(() -> new IllegalArgumentException("프로필 조회 실패 : userId에 해당하는 Profile 이 존재하지 않습니다."));
    profile.update(request);
    return ProfileDto.of(userId, profile);
  }

  @Override
  public void updateUserPassword(UUID userId, ChangePasswordRequest request) {

    User user = findUserById(userId);
    user.updatePassword(generateHashedPassword(request.password()));
  }

  @Override
  public UUID updateUserLock(UUID userId, UserLockUpdateRequest request) {


    return null;
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
}
