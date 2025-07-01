package project.closet.domain.users;

import java.util.UUID;
import project.closet.domain.users.dto.ChangePasswordRequest;
import project.closet.domain.users.dto.ProfileDto;
import project.closet.domain.users.dto.ProfileFindRequest;
import project.closet.domain.users.dto.ProfileUpdateWithImageUrlRequest;
import project.closet.domain.users.dto.UserCreateRequest;
import project.closet.domain.users.dto.UserDto;
import project.closet.domain.users.dto.UserLockUpdateRequest;
import project.closet.domain.users.dto.UserRoleUpdateRequest;

public interface UserService {

  UserDto getUsers(ProfileFindRequest dto);

  UserDto registerUser(UserCreateRequest dto);

  UserDto updateUserRole(UUID userId, UserRoleUpdateRequest request);

  ProfileDto getUserProfile(UUID userId);

  ProfileDto updateUserProfile(UUID userId, ProfileUpdateWithImageUrlRequest updateRequest);

  void updateUserPassword(UUID userId, ChangePasswordRequest request);

  UUID updateUserLock(UUID userId, UserLockUpdateRequest request);
}
