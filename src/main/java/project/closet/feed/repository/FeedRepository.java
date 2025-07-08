package project.closet.feed.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import project.closet.feed.entity.Feed;

public interface FeedRepository extends JpaRepository<Feed, UUID> {

}
