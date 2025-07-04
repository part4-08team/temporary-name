package project.closet.user.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import project.closet.dto.request.ChangePasswordRequest;
import project.closet.dto.request.ProfileUpdateRequest;
import project.closet.dto.request.UserCreateRequest;
import project.closet.dto.request.UserLockUpdateRequest;
import project.closet.dto.response.PageResponse;
import project.closet.dto.response.ProfileDto;
import project.closet.dto.response.UserDto;
import project.closet.user.controller.api.UserApi;
import project.closet.user.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserService userService;

    @GetMapping
    @Override
    public ResponseEntity<PageResponse<UserDto>> findAll() {
        new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @PostMapping
    @Override
    public ResponseEntity<UserDto> create(
            @RequestBody @Valid UserCreateRequest userCreateRequest
    ) {
        log.info("사용자 생성 요청: {}", userCreateRequest);
        UserDto createdUser = userService.create(userCreateRequest);
        log.debug("사용자 생성 응답: {}", createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{userId}/profiles")
    @Override
    public ResponseEntity<ProfileDto> getProfile(@PathVariable("userId") UUID userId) {
        log.info("사용자 프로필 조회 요청: userId={}", userId);
        ProfileDto profile = userService.getProfile(userId);
        log.debug("사용자 프로필 조회 응답: {}", profile);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping(
            value = "/{userId}/profile",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Override
    public ResponseEntity<ProfileDto> updateProfile(
            @PathVariable("userId") UUID userId,
            @RequestPart("request") @Valid ProfileUpdateRequest profileUpdateRequest,
            @RequestPart(value = "image", required = false) MultipartFile profile
    ) {
        return null;
    }

    @PatchMapping("/{userId}/password")
    @Override
    public ResponseEntity<Void> changePassword(
            @PathVariable("userId") UUID userId,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest
    ) {
        new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @PatchMapping("/{userId}/lock")
    @Override
    public ResponseEntity<String> changeAccountLockStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody @Valid UserLockUpdateRequest request
    ) {
        new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}
