package project.closet.notification.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import project.closet.dto.response.NotificationDtoCursorResponse;
import project.closet.exception.ErrorResponse;
import project.closet.security.ClosetUserDetails;

@Tag(name = "알림", description = "알림 API")
public interface NotificationApi {

    @Operation(summary = "알림 목록 조회", description = "알림 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "알림 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "알림 목록 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<NotificationDtoCursorResponse> findAll(Instant cursor, UUID idAfter, int limit, ClosetUserDetails closetUserDetails);

    @Operation(summary = "알림 읽음 처리", description = "알림 읽음 처리 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "알림 읽음 처리 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "알림 읽음 처리 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> delete(UUID notificationId);
}
