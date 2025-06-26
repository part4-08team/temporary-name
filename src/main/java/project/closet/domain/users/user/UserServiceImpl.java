package project.closet.domain.users.user;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.closet.domain.users.user.dto.ProfileDto;
import project.closet.domain.users.user.dto.ProfileFindRequest;
import project.closet.domain.users.user.dto.ProfileUpdateRequest;
import project.closet.domain.users.user.dto.UserCreateRequest;
import project.closet.domain.users.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  @Override
  public UserDto getUsers(ProfileFindRequest dto) {
    return null;
  }

  @Override
  public UserDto createUser(UserCreateRequest dto) {

    return null;
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
}
