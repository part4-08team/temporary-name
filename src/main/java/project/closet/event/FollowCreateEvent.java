package project.closet.event;

import java.util.UUID;

public record FollowCreateEvent(
        UUID followeeId,
        String followerName
) {

}
