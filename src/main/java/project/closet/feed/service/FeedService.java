package project.closet.feed.service;

import java.time.Instant;
import java.util.UUID;
import project.closet.dto.request.CommentCreateRequest;
import project.closet.dto.request.FeedCreateRequest;
import project.closet.dto.request.FeedUpdateRequest;
import project.closet.dto.response.CommentDto;
import project.closet.dto.response.CommentDtoCursorResponse;
import project.closet.dto.response.FeedDto;

public interface FeedService {

    FeedDto createFeed(FeedCreateRequest feedCreateRequest);

    void likeFeed(UUID feedId, UUID userId);

    void cancelFeedLike(UUID feedId, UUID userId);

    CommentDto createComment(CommentCreateRequest commentCreateRequest);

    void deleteFeed(UUID feedId);

    FeedDto updateFeed(UUID feedId, FeedUpdateRequest feedUpdateRequest, UUID loginUserId);

    CommentDtoCursorResponse getFeedComments(UUID feedId, Instant cursor, UUID idAfter, int limit);
}
