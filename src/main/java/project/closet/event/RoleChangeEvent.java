package project.closet.event;

import java.util.UUID;
import project.closet.user.entity.Role;

public record RoleChangeEvent(
        UUID userId,
        Role previousRole,
        Role newRole
) {

}
