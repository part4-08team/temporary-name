package project.closet.dto.request;

import java.util.UUID;
import project.closet.user.entity.Role;

public record RoleUpdateRequest(
        UUID userId,
        Role newRole
) {

}
