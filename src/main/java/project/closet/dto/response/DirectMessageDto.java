package project.closet.dto.response;

import java.time.Instant;
import java.util.UUID;

public record DirectMessageDto(
        UUID id,
        Instant createdAt,
        UserSummary sender,
        UserSummary receiver,
        String content
) {

}
