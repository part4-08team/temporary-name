package project.closet.follower.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import project.closet.follower.entity.Follow;

public interface FollowRepositoryCustom {

    List<Follow> findFollowingsWithCursor(
            UUID followerId,
            Instant cursor,
            UUID idAfter,
            String nameLike,
            int limit
    );

    List<Follow> findFollowersWithCursor(
            UUID followeeId,
            Instant cursor,
            UUID idAfter,
            String nameLike,
            int limit
    );

}
