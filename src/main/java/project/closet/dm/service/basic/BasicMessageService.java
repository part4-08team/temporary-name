package project.closet.dm.service.basic;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dm.entity.DirectMessage;
import project.closet.dm.repository.DirectMessageRepository;
import project.closet.dm.service.DirectMessageService;
import project.closet.dto.request.DirectMessageCreateRequest;
import project.closet.dto.response.DirectMessageDto;
import project.closet.dto.response.DirectMessageDtoCursorResponse;
import project.closet.dto.response.UserSummary;
import project.closet.event.DirectMessageSentEvent;
import project.closet.exception.user.UserNotFoundException;
import project.closet.storage.S3ContentStorage;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements DirectMessageService {

    private final UserRepository userRepository;
    private final DirectMessageRepository directMessageRepository;
    private final S3ContentStorage s3ContentStorage;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public DirectMessageDto sendMessage(DirectMessageCreateRequest directMessageCreateRequest) {
        UUID senderId = directMessageCreateRequest.senderId();
        User sender = userRepository.findByIdWithProfile(senderId)
                .orElseThrow(() -> UserNotFoundException.withId(senderId));
        String senderProfileImageUrl = sender.getProfile().getProfileImageKey();
        UserSummary senderSummary = UserSummary.from(sender, s3ContentStorage.getPresignedUrl(senderProfileImageUrl));

        UUID receiverId = directMessageCreateRequest.receiverId();
        User receiver = userRepository.findByIdWithProfile(receiverId)
                .orElseThrow(() -> UserNotFoundException.withId(receiverId));
        String receiverProfileImageUrl = receiver.getProfile().getProfileImageKey();
        UserSummary receiverSummary = UserSummary.from(receiver, s3ContentStorage.getPresignedUrl(receiverProfileImageUrl));

        // TODO 같은 유저 ID 로 메시지를 보내는 경우 예외 처리 추가
        String content = directMessageCreateRequest.content();
        DirectMessage directMessage = DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .build();
        directMessageRepository.save(directMessage);

        // 알림 생성 -> 알림을 수신하는 쪽에 이벤트 발생 + sse
        eventPublisher.publishEvent(new DirectMessageSentEvent(receiverId, sender.getName(), content));
        return new DirectMessageDto(
                directMessage,
                senderSummary,
                receiverSummary
        );
    }

    @Transactional(readOnly = true)
    @Override
    public DirectMessageDtoCursorResponse getDirectMessages(UUID targetUserId, Instant cursor, UUID idAfter, int limit, UUID loginUserId) {
        List<DirectMessage> messages = directMessageRepository.findDirectMessagesBetweenUsers(
                targetUserId, loginUserId, cursor, idAfter, limit + 1
        );

        boolean hasNext = messages.size() > limit;
        if (hasNext) {
            messages.remove(messages.size() - 1);
        }

        String nextCursor = null;
        UUID nextIdAfter = null;
        if (hasNext && !messages.isEmpty()) {
            DirectMessage last = messages.get(messages.size() - 1);
            nextCursor = last.getCreatedAt().toString();
            nextIdAfter = last.getId();
        }

        // 캐시를 사용하여 사용자 이미지 URL을 저장
        Map<UUID, String> userImageUrlCache = new HashMap<>();

        List<DirectMessageDto> dmDtos = messages.stream()
                .map(directMessage -> {
                    UUID senderId = directMessage.getSender().getId();
                    UUID receiverId = directMessage.getReceiver().getId();

                    String senderImageUrl = userImageUrlCache.computeIfAbsent(senderId, id -> {
                        String imageKey = directMessage.getSender().getProfile().getProfileImageKey();
                        return s3ContentStorage.getPresignedUrl(imageKey);
                    });

                    String receiverImageUrl = userImageUrlCache.computeIfAbsent(receiverId, id -> {
                        String imageKey = directMessage.getReceiver().getProfile().getProfileImageKey();
                        return s3ContentStorage.getPresignedUrl(imageKey);
                    });

                    UserSummary senderSummary = UserSummary.from(directMessage.getSender(), senderImageUrl);
                    UserSummary receiverSummary = UserSummary.from(directMessage.getReceiver(), receiverImageUrl);

                    return new DirectMessageDto(directMessage, senderSummary, receiverSummary);
                }).toList();

        long totalCount = directMessageRepository.countDirectMessagesBetweenUsers(targetUserId, loginUserId);

        return new DirectMessageDtoCursorResponse(
                dmDtos,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                "createdAt",
                SortDirection.DESCENDING
        );
    }
}
