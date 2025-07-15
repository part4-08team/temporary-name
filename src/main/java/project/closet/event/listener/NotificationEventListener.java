package project.closet.event.listener;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import project.closet.event.ClothesAttributeCreatEvent;
import project.closet.event.RoleChangeEvent;
import project.closet.notification.entity.NotificationLevel;
import project.closet.notification.service.NotificationService;
import project.closet.user.entity.Role;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // @Retryable 어노테이션 추가해서 재시도 기능 구현하기
    public void handle(RoleChangeEvent event) {
        UUID userId = event.userId();
        Role previousRole = event.previousRole();
        Role newRole = event.newRole();
        log.info("권한 변경 알림 이벤트 처리 시작: userId={}, previousRoleName={}, newRoleName={}",
                userId, previousRole.name(), newRole.name());
        try {
            notificationService.create(
                    userId,
                    String.format("권한 변경: %s -> %s", previousRole.name(), newRole.name()),
                    String.format("관리자에 의해 권한이 '%s'(으)로 변경되었습니다.", newRole.name()),
                    NotificationLevel.INFO
            );
            log.info("권한 변경 알림 이벤트 처리 완료: receiverId={}", userId);
        } catch (Exception e) {
            log.error("권한 변경 알림 이벤트 처리 실패: receiverId={}, error={}", userId, e.getMessage(), e);
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ClothesAttributeCreatEvent event) {
        log.info("새로운 의상 속성 추가 알림 이벤트 처리 시작: definitionName={}", event.definitionName());
        try {
            notificationService.createForAllUsers(
                    "새로운 의상 속성이 추가되었어요.",
                    String.format("내 의상에 '%s' 속성을 추가해보세요.", event.definitionName()),
                    NotificationLevel.INFO
            );
            log.info("새로운 의상 속성 추가 알림 이벤트 처리 완료");
        } catch (Exception e) {
            log.error("새로운 의상 속성 추가 알림 이벤트 처리 실패: error={}", e.getMessage(), e);
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }
}
