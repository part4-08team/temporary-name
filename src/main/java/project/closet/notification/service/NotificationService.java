package project.closet.notification.service;

import java.time.Instant;
import java.util.UUID;
import project.closet.dto.response.NotificationDtoCursorResponse;

public interface NotificationService {

    NotificationDtoCursorResponse getNotifications(UUID loginUserId, Instant cursor, UUID idAfter, int limit);

    void deleteNotification(UUID notificationId);
}
