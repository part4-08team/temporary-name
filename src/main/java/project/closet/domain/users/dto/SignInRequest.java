package project.closet.domain.users.dto;

import jakarta.validation.constraints.NotBlank;

public record SignInRequest(
    @NotBlank(message = "Email is required")
    String email,
    @NotBlank(message = "Password is required")
    String password
) {

}
