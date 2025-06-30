package project.closet.domain.users;

import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.users.dto.ResetPasswordRequest;
import project.closet.domain.users.dto.SignInRequest;
import project.closet.domain.users.dto.SignInResponse;

public interface AuthService {

  void logout(String accessToken);

  SignInResponse login(SignInRequest request);

  void resetPassword(ResetPasswordRequest request);

  String reissueAccessToken(String refreshToken);
}
