package project.closet.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import project.closet.dto.request.UserCreateRequest;
import project.closet.dto.response.ProfileDto;
import project.closet.dto.response.UserDto;

@Tag(name = "User", description = "User API")
public interface UserApi {

    @Operation(summary = "사용자 등록(회원가입) API")
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

    ResponseEntity<ProfileDto>
}
