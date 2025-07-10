package project.closet.feed.controller;

import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.SortDirection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dto.request.CommentCreateRequest;
import project.closet.dto.request.FeedCreateRequest;
import project.closet.dto.request.FeedUpdateRequest;
import project.closet.dto.response.CommentDto;
import project.closet.dto.response.CommentDtoCursorResponse;
import project.closet.dto.response.FeedDto;
import project.closet.dto.response.FeedDtoCursorResponse;
import project.closet.feed.controller.api.FeedApi;
import project.closet.feed.service.FeedService;
import project.closet.security.ClosetUserDetails;
import project.closet.weather.entity.PrecipitationType;
import project.closet.weather.entity.SkyStatus;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedController implements FeedApi {

    private final FeedService feedService;

    @GetMapping
    @Override
    public ResponseEntity<FeedDtoCursorResponse> getFeedList(
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "idAfter", required = false) UUID idAfter,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "sortBy") String sortBy,  // likeCount, createdAt
            @RequestParam(name = "sortDirection") SortDirection sortDirection,
            @RequestParam(name = "keywordLike", required = false) String keywordLike,
            @RequestParam(name = "skyStatusEqual", required = false) SkyStatus skyStatusEqual,
            @RequestParam(name = "precipitationType", required = false) PrecipitationType precipitationType,
            @RequestParam(name = "authorIdEqual", required = false) UUID authorIdEqual,
            @AuthenticationPrincipal ClosetUserDetails closetUserDetails
    ) {
        FeedDtoCursorResponse feedList = feedService.getFeedList(
                cursor, idAfter, limit, sortBy, sortDirection, keywordLike,
                skyStatusEqual, precipitationType, authorIdEqual, closetUserDetails.getUserId());
        return ResponseEntity.ok(feedList);
    }

    @PostMapping
    @Override
    public ResponseEntity<FeedDto> createFeed(
            @RequestBody @Valid FeedCreateRequest feedCreateRequest) {
        FeedDto feedDto = feedService.createFeed(feedCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(feedDto);
    }

    @PostMapping("{feedId}/like")
    @Override
    public ResponseEntity<Void> likeFeed(
            @PathVariable("feedId") UUID feedId,
            @AuthenticationPrincipal ClosetUserDetails closetUserDetails
    ) {
        UUID userId = closetUserDetails.getUserId();
        feedService.likeFeed(feedId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{feedId}/like")
    @Override
    public ResponseEntity<Void> cancelFeed(
            @PathVariable("feedId") UUID feedId,
            @AuthenticationPrincipal ClosetUserDetails closetUserDetails
    ) {
        UUID userId = closetUserDetails.getUserId();
        feedService.cancelFeedLike(feedId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{feedId}/comments")
    @Override
    public ResponseEntity<CommentDtoCursorResponse> getFeedComments(
            @PathVariable("feedId") UUID feedId,
            @RequestParam(name = "cursor", required = false) Instant cursor,
            @RequestParam(name = "idAfter", required = false) UUID idAfter,
            @RequestParam(name = "limit", defaultValue = "20") int limit
    ) {
        CommentDtoCursorResponse feedComments =
                feedService.getFeedComments(feedId, cursor, idAfter, limit);
        return ResponseEntity.ok(feedComments);
    }

    @PostMapping("/{feedId}/comments")
    @Override
    public ResponseEntity<CommentDto> createFeedComment(
            @PathVariable("feedId") UUID feedId,
            @RequestBody @Valid CommentCreateRequest commentCreateRequest
    ) {
        CommentDto commentDto = feedService.createComment(commentCreateRequest);
        return ResponseEntity.ok(commentDto);
    }

    @DeleteMapping("/{feedId}")
    @Override
    public ResponseEntity<Void> deleteFeed(@PathVariable("feedId") UUID feedId) {
        feedService.deleteFeed(feedId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{feedId}")
    @Override
    public ResponseEntity<FeedDto> updateFeed(
            @PathVariable("feedId") UUID feedId,
            @RequestBody @Valid FeedUpdateRequest feedUpdateRequest,
            @AuthenticationPrincipal ClosetUserDetails closetUserDetails
    ) {
        FeedDto feedDto = feedService.updateFeed(feedId, feedUpdateRequest,
                closetUserDetails.getUserId());
        return ResponseEntity.ok(feedDto);
    }
}
