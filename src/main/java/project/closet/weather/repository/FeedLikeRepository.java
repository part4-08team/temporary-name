package project.closet.weather.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import project.closet.feed.entity.Feed;
import project.closet.follower.entity.FeedLike;
import project.closet.user.entity.User;

public interface FeedLikeRepository extends JpaRepository<FeedLike, UUID> {

    boolean existsByUserAndFeed(User user, Feed feed);

    void deleteByUserAndFeed(User user, Feed feed);
}

/*
UNIQUE 제약조건에 인덱스로 인해서 User , Feed 순서로 조건문 태우는게 인덱스를 탑니다.
 */
