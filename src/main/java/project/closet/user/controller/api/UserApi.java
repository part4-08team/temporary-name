package project.closet.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.multipart.MultipartFile;
import project.closet.dto.request.ChangePasswordRequest;
import project.closet.dto.request.ProfileUpdateRequest;
import project.closet.dto.request.UserCreateRequest;
import project.closet.dto.request.UserLockUpdateRequest;
import project.closet.dto.response.PageResponse;
import project.closet.dto.response.ProfileDto;
import project.closet.dto.response.UserDto;

@Tag(name = "프로필 관리", description = "프로필 관리 API")
public interface UserApi {

    @Operation(summary = "계정 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "계정 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "404", description = "계정 목록 조회 실패(사용자 없음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<PageResponse<UserDto>> findAll();

    @Operation(summary = "사용자 등록(회원가입)")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "사용자 등록(회원가입) 성공",
                    content = @Content(schema = @Schema(implementation = UserDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "사용자 등록(회원가입) 실패",
                    content = @Content(examples = @ExampleObject(value = "User with email {email} already exists"))
            ),
    })
    ResponseEntity<UserDto> create(
            @Parameter(
                    description = "User 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            ) UserCreateRequest userCreateRequest
    );

    @Operation(summary = "프로필 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = ProfileDto.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "프로필 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<ProfileDto> getProfile(
            @Parameter(description = "조회할 User Id") UUID userId
    );

    @Operation(summary = "프로필 업데이트")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "프로필 업데이트 성공",
                    content = @Content(schema = @Schema(implementation = ProfileDto.class))
            ),
            @ApiResponse(
                    responseCode = "400", description = "프로필 업데이트 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<ProfileDto> updateProfile(
            @Parameter(description = "수정할 User Id") UUID userId,
            @Parameter(description = "수정할 User 정보") ProfileUpdateRequest profileUpdateRequest,
            @Parameter(description = "수정할 User 프로필 이미지") MultipartFile profile
    );

    @Operation(summary = "비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "비밀번호 변경 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "비밀번호 변경 실패(잘못된 요청)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404", description = "비밀번호 변경 실패(사용자 없음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> changePassword(
            @Parameter(description = "변경할 사용자 ID") UUID userId,
            @Parameter(description = "변경 비밀번호") ChangePasswordRequest changePasswordRequest
    );

    @Operation(
            summary = "계정 잠금 상태 변경",
            description = "[어드민 기능] 계정 잠금 상태를 변경합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "계정 잠금 상태 변경 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "계정 잠금 상태 변경 실패(사용자 없음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
    })
    ResponseEntity<String> changeAccountLockStatus(
            @Parameter(description = "사용자 ID") UUID userId,
            @Parameter(description = "잠금 상태") UserLockUpdateRequest request
    );

}
