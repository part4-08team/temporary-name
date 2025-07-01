package project.closet.domain.users.dto;

import jakarta.validation.constraints.NotNull;
import project.closet.domain.users.User.UserRole;

public record UserRoleUpdateRequest(
   @NotNull(message = "역할을 입력해주세요.")
   UserRole role) {
}
