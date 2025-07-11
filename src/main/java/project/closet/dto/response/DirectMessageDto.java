package project.closet.dto.response;

import java.time.Instant;
import java.util.UUID;
import project.closet.dm.entity.DirectMessage;

public record DirectMessageDto(
        UUID id,
        Instant createdAt,
        UserSummary sender,
        UserSummary receiver,
        String content
) {
    public DirectMessageDto(DirectMessage directMessage, UserSummary sender, UserSummary receiver) {
        this(
                directMessage.getId(),
                directMessage.getCreatedAt(),
                sender,
                receiver,
                directMessage.getContent()
        );
    }
}
