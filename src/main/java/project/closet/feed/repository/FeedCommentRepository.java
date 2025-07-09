package project.closet.feed.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import project.closet.feed.entity.FeedComment;

public interface FeedCommentRepository extends JpaRepository<FeedComment, UUID> {

}
