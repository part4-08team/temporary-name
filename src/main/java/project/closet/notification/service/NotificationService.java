package project.closet.notification.service;

import java.time.Instant;
import java.util.UUID;
import project.closet.dto.response.NotificationDtoCursorResponse;
import project.closet.notification.entity.NotificationLevel;

public interface NotificationService {

    NotificationDtoCursorResponse getNotifications(UUID loginUserId, Instant cursor, UUID idAfter, int limit);

    void deleteNotification(UUID notificationId);

    void create(UUID receiverId, String title, String content, NotificationLevel level);

    void createForAllUsers(String title, String content, NotificationLevel level);
}
