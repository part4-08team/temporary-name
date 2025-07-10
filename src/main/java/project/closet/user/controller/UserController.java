package project.closet.user.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.SortDirection;
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
import project.closet.dto.request.UserRoleUpdateRequest;
import project.closet.dto.response.PageResponse;
import project.closet.dto.response.ProfileDto;
import project.closet.dto.response.UserDto;
import project.closet.dto.response.UserDtoCursorResponse;
import project.closet.user.controller.api.UserApi;
import project.closet.user.entity.Role;
import project.closet.user.service.UserService;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController implements UserApi {

    private final UserService userService;

    @GetMapping
    @Override
    public ResponseEntity<UserDtoCursorResponse> findAll(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection,
            String emailLike,
            Role roleEqual,
            boolean locked
    ) {
        UserDtoCursorResponse response = userService.findAll(
                cursor, idAfter, limit, sortBy, sortDirection, emailLike, roleEqual, locked
        );
        return ResponseEntity.ok(response);
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

    // TODO : 프로필 업데이트 이미지 처리 로직 작업해야함
    @PatchMapping(
            value = "/{userId}/profiles",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Override
    public ResponseEntity<ProfileDto> updateProfile(
            @PathVariable(value = "userId") UUID userId,
            @RequestPart(value = "request") @Valid ProfileUpdateRequest profileUpdateRequest,
            @RequestPart(value = "image", required = false) MultipartFile profile
    ) {
        log.info("사용자 프로필 업데이트 요청: userId={}, request={}, image={}",
                userId, profileUpdateRequest,
                profile != null ? profile.getOriginalFilename() : "없음");
        ProfileDto profileDto = userService.updateProfile(userId, profileUpdateRequest, profile);
        log.debug("사용자 프로필 업데이트 응답: {}", profileDto);
        return ResponseEntity.ok(profileDto);
    }

    @PatchMapping("/{userId}/password")
    @Override
    public ResponseEntity<Void> changePassword(
            @PathVariable("userId") UUID userId,
            @RequestBody @Valid ChangePasswordRequest changePasswordRequest
    ) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @PatchMapping("/{userId}/lock")
    @Override
    public ResponseEntity<UUID> changeAccountLockStatus(
            @PathVariable("userId") UUID userId,
            @RequestBody @Valid UserLockUpdateRequest request
    ) {
        log.info("사용자 잠금 상태 변경 요청: userId={}, locked={}", userId, request.locked());
        UUID updatedUserId = userService.updateLockStatus(userId, request);
        log.debug("사용자 잠금 상태 변경 응답: {}", updatedUserId);
        return ResponseEntity.ok(updatedUserId);
    }

    @PostMapping("/{userId}/role")
    @Override
    public ResponseEntity<UserDto> updateRole(
            @PathVariable(value = "userId") UUID userId,
            @RequestBody @Valid UserRoleUpdateRequest userRoleUpdateRequest
    ) {
        log.info("권한 수정 요청");
        UserDto userDto = userService.updateRole(userId, userRoleUpdateRequest);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userDto);
    }
}
