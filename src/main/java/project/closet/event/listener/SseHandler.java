package project.closet.event.listener;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import project.closet.dto.response.NotificationDto;
import project.closet.event.NotificationCreatedEvent;
import project.closet.sse.SseService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseHandler {

    public static final String EVENT_NAME = "notifications";

    private final SseService sseService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(NotificationCreatedEvent event) {
        NotificationDto notification = event.notificationDto();
        UUID userId = notification.receiverId();
        sseService.send(userId, EVENT_NAME, notification);
    }

}
