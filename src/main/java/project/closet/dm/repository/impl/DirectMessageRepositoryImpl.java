package project.closet.dm.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import project.closet.dm.entity.DirectMessage;
import project.closet.dm.repository.DirectMessageRepositoryCustom;

@RequiredArgsConstructor
public class DirectMessageRepositoryImpl implements DirectMessageRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<DirectMessage> findDirectMessagesBetweenUsers(UUID targetUserId, UUID loginUserId, Instant cursor, UUID idAfter,
            int limit) {
        StringBuilder jpql = new StringBuilder("""
                SELECT m FROM DirectMessage m
                    JOIN FETCH m.sender s
                    LEFT JOIN FETCH s.profile
                    JOIN FETCH m.receiver r
                    LEFT JOIN FETCH r.profile
                WHERE (m.sender.id = :userA AND m.receiver.id = :userB)
                OR (m.sender.id = :userB AND m.receiver.id = :userA)
                """);

        Map<String, Object> params = new HashMap<>(
                Map.of("userA", targetUserId, "userB", loginUserId)
        );

        if (cursor != null) {
            jpql.append("AND (m.createdAt < :cursor OR (m.createdAt = :cursor AND m.id < :idAfter)) ");
            params.put("cursor", cursor);
            params.put("idAfter", idAfter);
        }

        jpql.append("ORDER BY m.createdAt DESC, m.id DESC");

        TypedQuery<DirectMessage> query = em.createQuery(jpql.toString(), DirectMessage.class)
                .setMaxResults(limit);

        params.forEach(query::setParameter);
        return query.getResultList();
    }

    @Override
    public long countDirectMessagesBetweenUsers(UUID targetUserId, UUID loginUserId) {
        String jpql = """
                SELECT COUNT(m) FROM DirectMessage m
                WHERE (m.sender.id = :loginUserId AND m.receiver.id = :targetUserId)
                OR (m.sender.id = :targetUserId AND m.receiver.id = :loginUserId)
                """;

        return em.createQuery(jpql, Long.class)
                .setParameter("loginUserId", loginUserId)
                .setParameter("targetUserId", targetUserId)
                .getSingleResult();
    }
}
