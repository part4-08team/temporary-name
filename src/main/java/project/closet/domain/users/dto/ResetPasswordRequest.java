package project.closet.domain.users.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
    @NotBlank(message = "Email is required")
    String email) {
}
