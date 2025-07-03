package project.closet.dto.request;

public record UserCreateRequest(
        String name,
        String email,
        String password
) {
}
