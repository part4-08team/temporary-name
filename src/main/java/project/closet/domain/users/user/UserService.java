package project.closet.domain.users.user;

import java.util.UUID;
import project.closet.domain.users.auth.dto.ChangePasswordRequest;
import project.closet.domain.users.user.dto.ProfileDto;
import project.closet.domain.users.user.dto.ProfileFindRequest;
import project.closet.domain.users.user.dto.ProfileUpdateWithImageUrlRequest;
import project.closet.domain.users.user.dto.UserCreateRequest;
import project.closet.domain.users.user.dto.UserDto;
import project.closet.domain.users.user.dto.UserLockUpdateRequest;
import project.closet.domain.users.user.dto.UserRoleUpdateRequest;

public interface UserService {

  UserDto getUsers(ProfileFindRequest dto);

  UserDto registerUser(UserCreateRequest dto);

  UserDto updateUserRole(UUID userId, UserRoleUpdateRequest request);

  ProfileDto getUserProfile(UUID userId);

  ProfileDto updateUserProfile(UUID userId, ProfileUpdateWithImageUrlRequest updateRequest);

  void updateUserPassword(UUID userId, ChangePasswordRequest request);

  UUID updateUserLock(UUID userId, UserLockUpdateRequest request);
}
