package project.closet.dm.service.basic;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dm.entity.DirectMessage;
import project.closet.dm.repository.DirectMessageRepository;
import project.closet.dm.service.DirectMessageService;
import project.closet.dto.request.DirectMessageCreateRequest;
import project.closet.dto.response.DirectMessageDto;
import project.closet.dto.response.UserSummary;
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
        DirectMessage directMessage = DirectMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .content(directMessageCreateRequest.content())
                .build();
        directMessageRepository.save(directMessage);

        return new DirectMessageDto(
                directMessage,
                senderSummary,
                receiverSummary
        );
    }
}
