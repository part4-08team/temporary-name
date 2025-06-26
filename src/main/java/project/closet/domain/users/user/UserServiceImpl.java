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
import project.closet.domain.users.user.dto.UserCreateRequest;
import project.closet.domain.users.user.dto.UserDto;
import project.closet.domain.users.user.dto.UserLockUpdateRequest;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

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

  @Override
  public UserDto updateUserRole(UUID userId) {
    return null;
  }

  @Override
  public ProfileDto getUserProfile(UUID userId) {
    return null;
  }

  @Override
  public ProfileDto updateUserProfile(UUID userId, ProfileUpdateRequest updateRequest) {
    return null;
  }

  @Override
  public void updateUserPassword(UUID userId, ChangePasswordRequest request) {

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
}
