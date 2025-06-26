package project.closet.domain.users.user;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.domain.users.auth.dto.ChangePasswordRequest;
import project.closet.domain.users.user.dto.ProfileDto;
import project.closet.domain.users.user.dto.ProfileFindRequest;
import project.closet.domain.users.user.dto.ProfileUpdateWithImageUrlRequest;
import project.closet.domain.users.user.dto.UserCreateRequest;
import project.closet.domain.users.user.dto.UserDto;
import project.closet.domain.users.user.dto.UserLockUpdateRequest;
import project.closet.domain.users.user.dto.UserRoleUpdateRequest;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // 계정 목록 조회
  @GetMapping
  public ResponseEntity<UserDto> getUsers(ProfileFindRequest request) {

    UserDto userDtoList = userService.getUsers(request);
    return ResponseEntity.status(HttpStatus.OK).body(userDtoList);
  }

  @PostMapping
  public ResponseEntity<UserDto> createUser(UserCreateRequest request) {

    UserDto userDto = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @PatchMapping("/{userId}/role")
  public ResponseEntity<UserDto> updateUserRole(
      @RequestParam("userId") UUID userId,
      @RequestBody @Valid UserRoleUpdateRequest request
  ) {

    UserDto userDto = userService.updateUserRole(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @GetMapping("/{userId}/profiles")
  public ResponseEntity<?> getUserProfiles(@RequestParam("userId") UUID userId) {

    ProfileDto userProfileDto = userService.getUserProfile(userId);
    return ResponseEntity.status(HttpStatus.OK).body(userProfileDto);
  }

  @PatchMapping("/{userId}/profiles")
  public ResponseEntity<?> updateUserProfiles(
      @RequestParam("userId") UUID userId,
      @RequestBody @Valid ProfileUpdateWithImageUrlRequest request) {

    ProfileDto userProfileDto = userService.updateUserProfile(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(userProfileDto);
  }

  @PatchMapping("/{userId}/password")
  public ResponseEntity<?> updateUserPassword(
      @RequestParam("userId") UUID userId,
      @RequestBody @Valid ChangePasswordRequest request) {

    userService.updateUserPassword(userId, request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PatchMapping("/{userId}/lock")
  public ResponseEntity<UUID> lockUser(
      @RequestParam("userId") UUID userId,
      @RequestBody @Valid UserLockUpdateRequest request) {

    UUID userUUID = userService.updateUserLock(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(userUUID);
  }
}
