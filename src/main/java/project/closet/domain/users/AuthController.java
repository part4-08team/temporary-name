package project.closet.domain.users;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.domain.users.dto.ResetPasswordRequest;
import project.closet.domain.users.dto.SignInRequest;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  // 로그 아웃
  @PostMapping("/sign-out")
  public ResponseEntity<?> logout() {

    return ResponseEntity.status(HttpStatus.OK).build();
  }

  // 로그인
  @PostMapping("/sign-in")
  public ResponseEntity<String> login(SignInRequest request) {

    return ResponseEntity.status(HttpStatus.OK).body("Login Success");
  }

  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<String> refresh(@RequestParam("refreshToken") String refreshToken) {


    return ResponseEntity.status(HttpStatus.OK).body("Success : Token Refreshed");
  }

  @GetMapping("/me")
  public ResponseEntity<String> findAccessToken(@RequestParam("refreshToken") String refreshToken) {

    // 인증정보 조회 완성
    return ResponseEntity.status(HttpStatus.OK).body("Success : Find Access Token");
  }
}
