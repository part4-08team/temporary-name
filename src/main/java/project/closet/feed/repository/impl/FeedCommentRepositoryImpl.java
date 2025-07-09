package project.closet.feed.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import project.closet.feed.entity.FeedComment;
import project.closet.feed.repository.FeedCommentRepositoryCustom;

@RequiredArgsConstructor
public class FeedCommentRepositoryImpl implements FeedCommentRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<FeedComment> findByFeedWithCursor(
            UUID feedId,
            Instant cursor,
            UUID idAfter,
            int limit
    ) {
        StringBuilder jpql = new StringBuilder("""
                    SELECT c FROM FeedComment c
                    JOIN FETCH c.author
                    WHERE c.feed.id = :feedId
                """);

        if (cursor != null) {
            jpql.append("""
                        AND (
                            c.createdAt > :cursor OR
                            (c.createdAt = :cursor AND c.id > :idAfter)
                        )
                    """);
        }

        jpql.append(" ORDER BY c.createdAt ASC, c.id ASC");

        TypedQuery<FeedComment> query = em.createQuery(jpql.toString(), FeedComment.class)
                .setParameter("feedId", feedId)
                .setMaxResults(limit);

        if (cursor != null) {
            query.setParameter("cursor", cursor);
            query.setParameter("idAfter", idAfter);
        }

        return query.getResultList();
    }
}
