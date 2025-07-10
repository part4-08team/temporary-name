package project.closet.dto.request;

import jakarta.validation.constraints.NotNull;
import project.closet.user.entity.Role;

public record UserRoleUpdateRequest(
        @NotNull
        Role role
) {

}
