package project.closet.notification.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import project.closet.notification.entity.Notification;

public interface NotificationRepositoryCustom {

    List<Notification> findAllByReceiverWithCursor(UUID receiverId, Instant cursor, UUID idAfter, int limit);

}
