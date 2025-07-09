package project.closet.dto.response;

import java.time.Instant;
import java.util.UUID;
import project.closet.feed.entity.FeedComment;

public record CommentDto(
        UUID id,
        Instant createdAt,
        UUID feedId,
        UserSummary author,
        String content
) {

    public static CommentDto from(FeedComment feedComment) {
        return new CommentDto(
                feedComment.getId(),
                feedComment.getCreatedAt(),
                feedComment.getFeed().getId(),
                UserSummary.from(feedComment.getAuthor()),
                feedComment.getContent()
        );
    }
}
