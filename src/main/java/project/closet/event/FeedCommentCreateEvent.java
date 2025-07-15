package project.closet.event;

import java.util.UUID;

public record FeedCommentCreateEvent(
        UUID feedAuthorId,
        String commenterUsername,
        String commentText
) {

}
