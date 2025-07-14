package project.closet.notification.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dto.response.NotificationDto;
import project.closet.dto.response.NotificationDtoCursorResponse;
import project.closet.notification.entity.Notification;
import project.closet.notification.repository.NotificationRepository;
import project.closet.notification.service.NotificationService;
import project.closet.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    @Override
    public NotificationDtoCursorResponse getNotifications(UUID loginUserId, Instant cursor, UUID idAfter, int limit) {
        List<Notification> notifications = notificationRepository.findAllByReceiverWithCursor(
                loginUserId, cursor, idAfter, limit + 1
        );

        boolean hasNext = notifications.size() > limit;
        if (hasNext) {
            notifications.remove(notifications.size() - 1);
        }

        List<NotificationDto> dtos = notifications.stream()
                .map(notification -> new NotificationDto(notification, loginUserId))
                .toList();

        Instant nextCursor = null;
        UUID nextIdAfter = null;

        if (!notifications.isEmpty()) {
            Notification lastNotification = notifications.get(notifications.size() - 1);
            nextCursor = lastNotification.getCreatedAt();
            nextIdAfter = lastNotification.getId();
        }

        long totalCount = notificationRepository.countByReceiverId(loginUserId);
        return new NotificationDtoCursorResponse(
                dtos,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                "createdAt",
                SortDirection.DESCENDING
        );
    }

    @Transactional
    @Override
    public void deleteNotification(UUID notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}
