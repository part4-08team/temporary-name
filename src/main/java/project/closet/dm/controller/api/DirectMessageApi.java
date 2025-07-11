package project.closet.dm.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import project.closet.dto.response.DirectMessageDtoCursorResponse;
import project.closet.exception.ErrorResponse;

@Tag(name = "DirectMessage", description = "DirectMessage API")
public interface DirectMessageApi {

    // DM 목록 조회
    @Operation(summary = "DM 목록 조회", description = "DM 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "DM 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "DM 목록 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<DirectMessageDtoCursorResponse> getDirectMessage(
            UUID userId,
            String cursor,
            UUID idAfter,
            int limit
    );
}
