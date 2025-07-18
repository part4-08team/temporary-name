package project.closet.feed.service;

import java.time.Instant;
import java.util.UUID;
import org.hibernate.query.SortDirection;
import project.closet.dto.request.CommentCreateRequest;
import project.closet.dto.request.FeedCreateRequest;
import project.closet.dto.request.FeedUpdateRequest;
import project.closet.dto.response.CommentDto;
import project.closet.dto.response.CommentDtoCursorResponse;
import project.closet.dto.response.FeedDto;
import project.closet.dto.response.FeedDtoCursorResponse;
import project.closet.weather.entity.PrecipitationType;
import project.closet.weather.entity.SkyStatus;

public interface FeedService {

    FeedDto createFeed(FeedCreateRequest feedCreateRequest);

    void likeFeed(UUID feedId, UUID loginUserId);

    void cancelFeedLike(UUID feedId, UUID userId);

    CommentDto createComment(CommentCreateRequest commentCreateRequest);

    void deleteFeed(UUID feedId);

    FeedDto updateFeed(UUID feedId, FeedUpdateRequest feedUpdateRequest, UUID loginUserId);

    CommentDtoCursorResponse getFeedComments(UUID feedId, Instant cursor, UUID idAfter, int limit);

    FeedDtoCursorResponse getFeedList(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection,
            String keywordLike,
            SkyStatus skyStatusEqual,
            PrecipitationType precipitationType,
            UUID authorIdEqual,
            UUID loginUserId
    );
}
