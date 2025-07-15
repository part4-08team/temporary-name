package project.closet.event;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record FeedCreatedEvent(
        Set<UUID> receiverIds,
        String authorName,
        String content
) {

    public FeedCreatedEvent(List<UUID> receiverIds, String authorName, String content) {
        this(Set.copyOf(receiverIds), authorName, content);
    }
}
