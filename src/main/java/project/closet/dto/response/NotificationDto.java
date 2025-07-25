package project.closet.dto.response;

import java.time.Instant;
import java.util.UUID;
import project.closet.notification.entity.Notification;
import project.closet.notification.entity.NotificationLevel;

public record NotificationDto(
        UUID id,
        Instant createdAt,
        UUID receiverId,
        String title,
        String content,
        NotificationLevel level
) {

    public NotificationDto(Notification notification) {
        this(
                notification.getId(),
                notification.getCreatedAt(),
                notification.getReceiverId(),
                notification.getTitle(),
                notification.getContent(),
                notification.getLevel()
        );
    }
}
