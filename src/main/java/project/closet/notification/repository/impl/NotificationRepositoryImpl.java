package project.closet.notification.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import project.closet.notification.entity.Notification;
import project.closet.notification.repository.NotificationRepositoryCustom;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Notification> findAllByReceiverWithCursor(UUID receiverId, Instant cursor, UUID idAfter, int limit) {
        StringBuilder jpql = new StringBuilder("""
                SELECT n FROM Notification n
                WHERE n.receiverId = :receiverId
                """);

        if (cursor != null && idAfter != null) {
            jpql.append("AND (n.createdAt < :cursor OR (n.createdAt = :cursor AND n.id < :idAfter)) ");
        }

        jpql.append("ORDER BY n.createdAt DESC, n.id DESC");

        TypedQuery<Notification> query = em.createQuery(jpql.toString(), Notification.class)
                .setParameter("receiverId", receiverId)
                .setMaxResults(limit);

        if (cursor != null && idAfter != null) {
            query.setParameter("cursor", cursor);
            query.setParameter("idAfter", idAfter);
        }

        return query.getResultList();
    }
}
