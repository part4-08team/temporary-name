package project.closet.dto.request;

import java.util.UUID;

public record FollowCreateRequest(
        UUID followeeId,
        UUID followerId
) {

}
