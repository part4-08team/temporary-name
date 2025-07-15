package project.closet.notification.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.SortDirection;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dto.response.NotificationDto;
import project.closet.dto.response.NotificationDtoCursorResponse;
import project.closet.event.NotificationCreatedEvent;
import project.closet.notification.entity.Notification;
import project.closet.notification.entity.NotificationLevel;
import project.closet.notification.repository.NotificationRepository;
import project.closet.notification.service.NotificationService;
import project.closet.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicNotificationService implements NotificationService {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

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
                .map(NotificationDto::new)
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

    @Transactional
    @Override
    public void create(UUID receiverId, String title, String content, NotificationLevel level) {
        log.debug("새 알림 생성 시작: receiverId={}", receiverId);
        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .title(title)
                .content(content)
                .level(level)
                .build();

        notificationRepository.save(notification);
        log.info("새 알림 생성 완료: id={}, receiverId={}", notification.getId(), receiverId);
        // SSE 이벤트 발생
        NotificationDto notificationDto = new NotificationDto(notification);
        eventPublisher.publishEvent(new NotificationCreatedEvent(notificationDto));
    }

    @Transactional
    @Override
    public void createForAllUsers(String title, String content, NotificationLevel level) {
        log.debug("모든 사용자에게 새 알림 생성 시작");
        List<UUID> allIds = userRepository.findAllIds();

        List<Notification> notifications = allIds.stream()
                .map(id -> Notification.builder()
                        .receiverId(id)
                        .title(title)
                        .content(content)
                        .level(level)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
        log.info("모든 사용자에게 새 알림 생성 완료: title={}, content={}", title, content);

        // sse 이벤트 발생
        notifications.stream()
                .map(NotificationDto::new)
                .forEach(notificationDto -> eventPublisher.publishEvent(new NotificationCreatedEvent(notificationDto)));
    }
}
