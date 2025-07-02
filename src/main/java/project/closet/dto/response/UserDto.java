package project.closet.dto.response;

import java.util.UUID;
import project.closet.user.entity.Role;

public record UserDto(
        UUID id,
        String username,
        Role role
) {

}
