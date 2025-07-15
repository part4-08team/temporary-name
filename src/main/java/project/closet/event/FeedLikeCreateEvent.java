package project.closet.event;

import java.util.UUID;

public record FeedLikeCreateEvent(
        UUID feedAuthorId,
        String likerUsername,
        String feedContent
) {

}
