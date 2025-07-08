package project.closet.feed.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dto.request.CommentCreateRequest;
import project.closet.dto.request.FeedCreateRequest;
import project.closet.dto.request.FeedUpdateRequest;
import project.closet.dto.response.CommentDto;
import project.closet.dto.response.CommentDtoCursorResponse;
import project.closet.dto.response.FeedDto;
import project.closet.dto.response.FeedDtoCursorResponse;
import project.closet.feed.controller.api.FeedApi;
import project.closet.weather.entity.PrecipitationType;
import project.closet.weather.entity.SkyStatus;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/feeds")
public class FeedController implements FeedApi {

    @GetMapping
    @Override
    public ResponseEntity<FeedDtoCursorResponse> getFeedList(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            String sortDirection,
            String keywordLike,
            SkyStatus skyStatusEqual,
            PrecipitationType precipitationTypeEqual,
            UUID authorIdEqual
    ) {
        return null;
    }

    @PostMapping
    @Override
    public ResponseEntity<FeedDto> createFeed(@RequestBody @Valid FeedCreateRequest feedCreateRequest) {
        return null;
    }

    @PostMapping("{feedId}/like")
    @Override
    public ResponseEntity<FeedDto> likeFeed(@PathVariable("feedId") UUID feedId) {
        return null;
    }

    @DeleteMapping("{feedId}/like")
    @Override
    public ResponseEntity<Void> cancelFeed(@PathVariable("feedId") UUID feedId) {
        return null;
    }

    @GetMapping("/{feedId}/comments")
    @Override
    public ResponseEntity<CommentDtoCursorResponse> getFeedComments(
            @PathVariable("feedId") UUID feedId,
            String cursor,
            UUID idAfter,
            int limit
    ) {
        return null;
    }

    @PostMapping("/{feedId}/comments")
    @Override
    public ResponseEntity<CommentDto> createFeedComment(
            @PathVariable("feedId") UUID feedId,
            @RequestBody @Valid CommentCreateRequest commentCreateRequest
    ) {
        return null;
    }

    @DeleteMapping("/{feedId}")
    @Override
    public ResponseEntity<Void> deleteFeed(@PathVariable("feedId") UUID feedId) {
        return null;
    }

    @PatchMapping("/{feedId}")
    @Override
    public ResponseEntity<FeedDto> updateFeed(
            @PathVariable("feedId") UUID feedId,
            @RequestBody @Valid FeedUpdateRequest feedUpdateRequest
    ) {
        return null;
    }
}
