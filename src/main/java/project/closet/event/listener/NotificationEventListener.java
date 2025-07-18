package project.closet.event.listener;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import project.closet.event.ClothesAttributeCreatEvent;
import project.closet.event.ClothesAttributeUpdateEvent;
import project.closet.event.DirectMessageSentEvent;
import project.closet.event.FeedCommentCreateEvent;
import project.closet.event.FeedCreatedEvent;
import project.closet.event.FeedLikeCreateEvent;
import project.closet.event.FollowCreateEvent;
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
            log.error("권한 변경 알림 이벤트 처리 실패: receiverId={}, error={}", userId, e.getMessage());
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
            log.error("새로운 의상 속성 추가 알림 이벤트 처리 실패: error={}", e.getMessage());
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(ClothesAttributeUpdateEvent event) {
        log.info("의상 속성 업데이트 알림 이벤트 처리 시작: definitionName={}", event.definitionName());
        try {
            notificationService.createForAllUsers(
                    "의상 속성이 변경되었어요.",
                    String.format("[%s] 속성을 확인해보세요.", event.definitionName()),
                    NotificationLevel.INFO
            );
            log.info("의상 속성 업데이트 알림 이벤트 처리 완료");
        } catch (Exception e) {
            log.error("의상 속성 업데이트 알림 이벤트 처리 실패: error={}", e.getMessage());
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FeedLikeCreateEvent event) {
        UUID receiverId = event.feedAuthorId();
        String username = event.likerUsername();
        String content = event.feedContent();
        log.info("피드 좋아요 알림 이벤트 처리 시작: receiverId ={}, name={}, content={}", receiverId, username, content);
        try {
            notificationService.create(
                    receiverId,
                    String.format("%s님이 내 피드를 좋아합니다.", username),
                    content,
                    NotificationLevel.INFO
            );
        } catch (Exception e) {
            log.error("피드 좋아요 알림 이벤트 처리 실패: receiverId={}, error={}", receiverId, e.getMessage());
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FeedCommentCreateEvent event) {
        UUID receiverId = event.feedAuthorId();
        String commenterUsername = event.commenterUsername();
        String commentText = event.commentText();
        log.info("피드 댓글 알림 이벤트 처리 시작: receiverId={}, commenterUsername={}, commentText={}", receiverId, commenterUsername, commentText);
        try {
            notificationService.create(
                    receiverId,
                    String.format("%s님이 댓글을 달았어요.", commenterUsername),
                    commentText,
                    NotificationLevel.INFO
            );
        } catch (Exception e) {
            log.error("피드 댓글 알림 이벤트 처리 실패: receiverId={}, error={}", receiverId, e.getMessage());
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FollowCreateEvent event) {
        UUID receiverId = event.followeeId();
        String followerUsername = event.followerName();
        log.info("팔로우 알림 이벤트 처리 시작: receiverId={}, followerUsername={}", receiverId, followerUsername);
        try {
            notificationService.create(
                    receiverId,
                    String.format("%s님이 나를 팔로우했어요.", followerUsername),
                    "",
                    NotificationLevel.INFO
            );
        } catch (Exception e) {
            log.error("팔로우 알림 이벤트 처리 실패: receiverId={}, error={}", receiverId, e.getMessage());
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(DirectMessageSentEvent event) {
        UUID receiverId = event.receiverId();
        String senderUsername = event.senderUsername();
        String content = event.messageContent();
        log.info("새로운 DM 알림 이벤트 처리 시작: receiverId={}, senderUsername={}, content={}", receiverId, senderUsername, content);
        try {
            notificationService.create(
                    receiverId,
                    String.format("[DM] %s", senderUsername),
                    content,
                    NotificationLevel.INFO
            );
        } catch (Exception e) {
            log.error("새로운 DM 알림 이벤트 처리 실패: receiverId={}, error={}", receiverId, e.getMessage());
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }

    @Async("eventTaskExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(FeedCreatedEvent event) {
        Set<UUID> receiverIds = event.receiverIds();
        String authorUsername = event.authorName();
        String feedContent = event.content();
        try {
            notificationService.createAll(
                    receiverIds,
                    String.format("%s님이 새로운 피드를 작성했어요.", authorUsername),
                    feedContent,
                    NotificationLevel.INFO
            );
        } catch (Exception e) {
            log.error("피드 생성 알림 이벤트 처리 실패: error={}", e.getMessage());
            throw e;    // Retryable 예외 발생시켜 재시도 로직 시도
        }
    }
}
