package project.closet.domain.users;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.closet.domain.users.dto.ChangePasswordRequest;
import project.closet.domain.users.dto.ProfileDto;
import project.closet.domain.users.dto.ProfileFindRequest;
import project.closet.domain.users.dto.ProfileUpdateWithImageUrlRequest;
import project.closet.domain.users.dto.UserCreateRequest;
import project.closet.domain.users.dto.UserDto;
import project.closet.domain.users.dto.UserLockUpdateRequest;
import project.closet.domain.users.dto.UserRoleUpdateRequest;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  // 계정 목록 조회
  @GetMapping
  public ResponseEntity<UserDto> getUsers(@RequestBody @Valid ProfileFindRequest request) {

    // todo : 반환값 API에 맞춰서 바꾸기
    UserDto userDtoList = userService.getUsers(request);
    return ResponseEntity.status(HttpStatus.OK).body(userDtoList);
  }

  @PostMapping
  public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateRequest request) {

    UserDto userDto = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @PatchMapping("/{userId}/role")
  public ResponseEntity<UserDto> updateUserRole(
      @PathVariable("userId") UUID userId,
      @RequestBody @Valid UserRoleUpdateRequest request
  ) {

    UserDto userDto = userService.updateUserRole(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(userDto);
  }

  @GetMapping("/{userId}/profiles")
  public ResponseEntity<ProfileDto> getUserProfiles(@PathVariable("userId") UUID userId) {

    ProfileDto userProfileDto = userService.getUserProfile(userId);
    return ResponseEntity.status(HttpStatus.OK).body(userProfileDto);
  }

  @PatchMapping("/{userId}/profiles")
  public ResponseEntity<ProfileDto> updateUserProfiles(
      @PathVariable("userId") UUID userId,
      @RequestBody @Valid ProfileUpdateWithImageUrlRequest request) {

    ProfileDto userProfileDto = userService.updateUserProfile(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(userProfileDto);
  }

  @PatchMapping("/{userId}/password")
  public ResponseEntity<?> updateUserPassword(
      @PathVariable("userId") UUID userId,
      @RequestBody @Valid ChangePasswordRequest request) {

    userService.updateUserPassword(userId, request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PatchMapping("/{userId}/lock")
  public ResponseEntity<UUID> lockUser(
      @PathVariable("userId") UUID userId,
      @RequestBody @Valid UserLockUpdateRequest request) {

    UUID userUUID = userService.updateUserLock(userId, request);
    return ResponseEntity.status(HttpStatus.OK).body(userUUID);
  }
}
