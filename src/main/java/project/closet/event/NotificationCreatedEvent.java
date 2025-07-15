package project.closet.event;

import java.time.Instant;
import project.closet.dto.response.NotificationDto;

public record NotificationCreatedEvent(
        Instant createdAt,
        NotificationDto notificationDto
) {

    public NotificationCreatedEvent(NotificationDto notificationDto) {
        this(Instant.now(), notificationDto);
    }
}
