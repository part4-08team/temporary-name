package project.closet.feed.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.query.SortDirection;
import org.springframework.http.ResponseEntity;
import project.closet.dto.request.CommentCreateRequest;
import project.closet.dto.request.FeedCreateRequest;
import project.closet.dto.request.FeedUpdateRequest;
import project.closet.dto.response.CommentDto;
import project.closet.dto.response.CommentDtoCursorResponse;
import project.closet.dto.response.FeedDto;
import project.closet.dto.response.FeedDtoCursorResponse;
import project.closet.security.ClosetUserDetails;
import project.closet.weather.entity.PrecipitationType;
import project.closet.weather.entity.SkyStatus;

@Tag(name = "피드 관리", description = "피드 관련 API")
public interface FeedApi {

    // 피드 목록 조회
    @Operation(summary = "피드 목록 조회", description = "피드 목록 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "피드 목록 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "피드 목록 조회 실패"
            )
    })
    ResponseEntity<FeedDtoCursorResponse> getFeedList(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection,
            String keywordLike,
            SkyStatus skyStatusEqual,
            PrecipitationType precipitationTypeEqual,
            UUID authorIdEqual,
            @Parameter(hidden = true) ClosetUserDetails closetUserDetails
    );

    // 피드 등록
    @Operation(summary = "피드 등록", description = "피드 등록 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", description = "피드 등록 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "피드 등록 실패"
            )
    })
    ResponseEntity<FeedDto> createFeed(@Parameter FeedCreateRequest feedCreateRequest);

    // 피드 좋아요
    @Operation(summary = "피드 좋아요", description = "피드 좋아요 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "피드 좋아요 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "피드 좋아요 실패"
            )
    })
    ResponseEntity<Void> likeFeed(UUID feedId,
            @Parameter(hidden = true) ClosetUserDetails closetUserDetails);

    // 피드 좋아요 취소
    @Operation(summary = "피드 좋아요 취소", description = "피드 좋아요 취소 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "피드 좋아요 취소 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "피드 좋아요 취소 실패"
            )
    })
    ResponseEntity<Void> cancelFeed(UUID feedId,
            @Parameter(hidden = true)
            ClosetUserDetails closetUserDetails);

    // 피드 댓글 조회
    @Operation(summary = "피드 댓글 조회", description = "피드 댓글 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "피드 댓글 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "피드 댓글 조회 실패"
            )
    })
    ResponseEntity<CommentDtoCursorResponse> getFeedComments(
            UUID feedId,
            Instant cursor,
            UUID idAfter,
            int limit
    );

    // 피드 댓글 등록
    @Operation(summary = "피드 댓글 등록", description = "피드 댓글 등록 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "피드 댓글 등록 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "피드 댓글 등록 실패"
            )
    })
    ResponseEntity<CommentDto> createFeedComment(UUID feedId,
            CommentCreateRequest commentCreateRequest);

    // 피드 삭제
    @Operation(summary = "피드 삭제", description = "피드 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "피드 삭제 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "피드 삭제 실패"
            )
    })
    ResponseEntity<Void> deleteFeed(UUID feedId);

    // 피드 수정
    @Operation(summary = "피드 수정", description = "피드 수정 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "피드 수정 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "피드 수정 실패"
            )
    })
    ResponseEntity<FeedDto> updateFeed(
            UUID feedId,
            FeedUpdateRequest feedUpdateRequest,
            @Parameter(hidden = true) ClosetUserDetails closetUserDetails
    );
}
