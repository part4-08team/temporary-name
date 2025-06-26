package project.closet.domain.users.auth.dto;

public record SignInRequest(
    String email,
    String password
) {

}
