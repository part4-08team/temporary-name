package project.closet.dto.response;

import java.util.UUID;
import project.closet.follower.entity.Follow;

public record FollowDto(
        UUID id,
        UserSummary followee,
        UserSummary follower
) {

    public static FollowDto from(Follow follow) {
        return new FollowDto(
                follow.getId(),
                new UserSummary(
                        follow.getFollowee().getId(),
                        follow.getFollowee().getName(),
                        follow.getFollowee().getProfile().getProfileImageUrl()
                ),
                new UserSummary(
                        follow.getFollower().getId(),
                        follow.getFollower().getName(),
                        follow.getFollower().getProfile().getProfileImageUrl()
                )
        );
    }
}
