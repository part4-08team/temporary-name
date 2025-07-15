package project.closet.feed.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.closet.feed.entity.Feed;

public interface FeedRepository extends JpaRepository<Feed, UUID>, FeedRepositoryCustom {

    @Query("""
                SELECT f FROM Feed f
                JOIN FETCH f.author
                JOIN FETCH f.weather
                LEFT JOIN FETCH f.feedClothesList fc
                LEFT JOIN FETCH fc.clothes
                WHERE f.id = :feedId
            """)
    Optional<Feed> findByIdWithAll(UUID feedId);

    @Query("""
            SELECT f FROM Feed f
            JOIN FETCH f.author
            WHERE f.id = :feedId
            """)
    Optional<Feed> findByIdWithAuthor (UUID feedId);
}
