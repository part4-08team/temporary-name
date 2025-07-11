package project.closet.dm.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import project.closet.dto.response.DirectMessageDtoCursorResponse;

@Tag(name = "DirectMessage", description = "DirectMessage API")
public interface DirectMessageApi {

    // DM 목록 조회
    @Operation(summary = "DM 목록 조회", description = "DM 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(),
            @ApiResponse()
    })
    ResponseEntity<DirectMessageDtoCursorResponse> getDirectMessage();
}
