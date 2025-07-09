package project.closet.feed.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import project.closet.feed.entity.FeedComment;

public interface FeedCommentRepositoryCustom {

    List<FeedComment> findByFeedWithCursor(UUID feedId, Instant cursor, UUID idAfter, int limitPlusOne);

}
