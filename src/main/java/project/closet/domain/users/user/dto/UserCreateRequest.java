package project.closet.domain.users.user.dto;

public record UserCreateRequest(
    String name,
    String email,
    String password
) {
}
