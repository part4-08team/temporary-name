package project.closet.domain.users.user;

import java.util.UUID;
import project.closet.domain.users.auth.dto.ChangePasswordRequest;
import project.closet.domain.users.user.dto.ProfileDto;
import project.closet.domain.users.user.dto.ProfileFindRequest;
import project.closet.domain.users.user.dto.ProfileUpdateRequest;
import project.closet.domain.users.user.dto.UserCreateRequest;
import project.closet.domain.users.user.dto.UserDto;
import project.closet.domain.users.user.dto.UserLockUpdateRequest;

public interface UserService {

  UserDto getUsers(ProfileFindRequest dto);

  UserDto createUser(UserCreateRequest dto);

  UserDto updateUserRole(UUID userId);

  ProfileDto getUserProfile(UUID userId);

  ProfileDto updateUserProfile(UUID userId, ProfileUpdateRequest updateRequest);

  void updateUserPassword(UUID userId, ChangePasswordRequest request);

  UUID updateUserLock(UUID userId, UserLockUpdateRequest request);
}
