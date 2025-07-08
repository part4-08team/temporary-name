package project.closet.follower.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import project.closet.follower.entity.Follow;

@RequiredArgsConstructor
public class FollowRepositoryImpl implements FollowRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Follow> findFollowingsWithCursor(
            UUID followerId,
            Instant cursor,
            UUID idAfter,
            String nameLike,
            int limit
    ) {
        StringBuilder jpql = new StringBuilder(
                "SELECT f FROM Follow f " +
                        "JOIN FETCH f.followee fe " +
                        "LEFT JOIN FETCH fe.profile p " +
                        "WHERE f.follower.id = :followerId "
        );

        if (cursor != null && idAfter != null) {
            jpql.append(
                    "AND (f.createdAt < :cursor OR (f.createdAt = :cursor AND f.id < :idAfter)) ");
        }

        if (nameLike != null && !nameLike.isBlank()) {
            jpql.append("AND fe.name LIKE :nameLike ");
        }

        jpql.append("ORDER BY f.createdAt DESC, f.id DESC");

        TypedQuery<Follow> query = em.createQuery(jpql.toString(), Follow.class);
        query.setParameter("followerId", followerId);

        if (cursor != null && idAfter != null) {
            query.setParameter("cursor", cursor);
            query.setParameter("idAfter", idAfter);
        }

        if (nameLike != null && !nameLike.isBlank()) {
            query.setParameter("nameLike", "%" + nameLike + "%");
        }

        query.setMaxResults(limit + 1);
        return query.getResultList();
    }

    @Override
    public List<Follow> findFollowersWithCursor(UUID followeeId, Instant cursor, UUID idAfter,
            String nameLike, int limit) {
        StringBuilder jpql = new StringBuilder(
                "SELECT f FROM Follow f " +
                        "JOIN FETCH f.follower fo " +
                        "LEFT JOIN FETCH fo.profile p " +
                        "WHERE f.followee.id = :followeeId "
        );

        if (cursor != null && idAfter != null) {
            jpql.append(
                    "AND (f.createdAt < :cursor OR (f.createdAt = :cursor AND f.id < :idAfter)) ");
        }

        if (nameLike != null && !nameLike.isBlank()) {
            jpql.append("AND fo.name LIKE :nameLike ");
        }

        jpql.append("ORDER BY f.createdAt DESC, f.id DESC");

        TypedQuery<Follow> query = em.createQuery(jpql.toString(), Follow.class);
        query.setParameter("followeeId", followeeId);

        if (cursor != null && idAfter != null) {
            query.setParameter("cursor", cursor);
            query.setParameter("idAfter", idAfter);
        }

        if (nameLike != null && !nameLike.isBlank()) {
            query.setParameter("nameLike", "%" + nameLike + "%");
        }

        query.setMaxResults(limit + 1);
        return query.getResultList();
    }
}
