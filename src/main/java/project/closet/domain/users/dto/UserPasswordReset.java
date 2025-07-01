package project.closet.domain.users.dto;

public record UserPasswordReset(
    String email,
    String password
) {
}
