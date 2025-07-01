package project.closet.domain.users;

import project.closet.domain.users.dto.ResetPasswordRequest;
import project.closet.domain.users.dto.SignInRequest;
import project.closet.domain.users.dto.SignInResponse;

public interface AuthService {

  void initAdmin();

  void logout(String accessToken);

  SignInResponse login(SignInRequest request);

  void resetPassword(ResetPasswordRequest request);

  String reissueAccessToken(String refreshToken);

  String getAccessToken(String refreshToken);
}
