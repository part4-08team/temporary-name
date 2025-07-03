package project.closet.dto.response;

import java.util.UUID;
import project.closet.user.entity.Role;
import project.closet.user.entity.User;

public record UserDto(
        UUID id,
        String username,
        Role role
) {

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getRole()
        );
    }
}
