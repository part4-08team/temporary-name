package project.closet.domain.users.dto;

import java.util.Objects;

public record SignInResponse(
    String accessToken,
    String refreshToken) {

  public SignInResponse {
    Objects.requireNonNull(accessToken, "AccessToken must not be null");
    Objects.requireNonNull(refreshToken, "RefreshToken must not be null");
  }
}
