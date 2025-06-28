package project.closet.domain.users.dto;

public record SignInRequest(
    String email,
    String password
) {

}
