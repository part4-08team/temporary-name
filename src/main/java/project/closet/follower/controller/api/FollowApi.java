package project.closet.follower.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import project.closet.dto.request.FollowCreateRequest;
import project.closet.dto.response.FollowDto;
import project.closet.dto.response.FollowListResponse;
import project.closet.dto.response.FollowSummaryDto;
import project.closet.security.ClosetUserDetails;

@Tag(name = "팔로우 관리", description = "팔로우 관련 API")
public interface FollowApi {

    // 팔로우 생성
    @Operation(summary = "팔로우 생성")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "팔로우 생성 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "팔로우 생성 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<FollowDto> createFollow(FollowCreateRequest followCreateRequest);

    // 팔로우 요약 정보 조회
    @Operation(summary = "팔로우 요약 정보 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "팔로우 요약 정보 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "팔로우 요약 정보 조회 실패(사용자 없음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<FollowSummaryDto> getFollowSummary(UUID userId, ClosetUserDetails userDetails);

    // 팔로잉 목록 조회
    @Operation(summary = "팔로잉 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "팔로잉 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "팔로잉 목록 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<FollowListResponse> getFollowingList(
            UUID followerId,
            String cursor,
            UUID idAfter,
            int limit,
            String nameLike
    );

    // 팔로워 목록 조회
    @Operation(summary = "팔로워 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "팔로워 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "팔로워 목록 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> getFolloweeList(
            UUID followeeId,
            String cursor,
            UUID idAfter,
            int limit,
            String nameLike
    );

    // 팔로우 취소
    @Operation(summary = "팔로우 취소")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "팔로우 취소 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "팔로우 취소 실패(팔로우 없음)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> cancelFollow(
            UUID followId
    );
}
