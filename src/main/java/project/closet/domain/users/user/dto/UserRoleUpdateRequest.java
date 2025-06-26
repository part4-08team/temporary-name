package project.closet.domain.users.user.dto;

import jakarta.validation.constraints.NotBlank;
import project.closet.domain.users.user.UserRole;

public record UserRoleUpdateRequest(
   @NotBlank(message = "역할을 입력해주세요.")
   UserRole role) {
}
