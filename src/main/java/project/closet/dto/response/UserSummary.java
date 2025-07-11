package project.closet.dto.response;

import java.util.UUID;
import project.closet.user.entity.User;

public record UserSummary(
        UUID userId,
        String name,
        String profileImageUrl
) {

    public static UserSummary from(User user, String presignedUrl) {
        return new UserSummary(
                user.getId(),
                user.getName(),
                presignedUrl
        );
    }
}
