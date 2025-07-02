package project.closet.user.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import project.closet.dto.response.UserDto;

@Tag(name = "User", description = "User API")
public interface UserApi {

    @Operation(summary = "User 등록")
    ResponseEntity<UserDto> create();
}
