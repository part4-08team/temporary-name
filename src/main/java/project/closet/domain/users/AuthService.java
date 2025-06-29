package project.closet.domain.users;

import project.closet.domain.users.dto.ResetPasswordRequest;

public interface AuthService {

  void resetPassword(ResetPasswordRequest request);

  String reissueAccessToken(String refreshToken);
}
