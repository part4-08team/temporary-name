package project.closet.dto.response;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(
        UUID id,
        Instant createdAt,
        UUID feedId,
        UserSummary author,
        String content
) {

}
