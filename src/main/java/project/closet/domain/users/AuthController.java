package project.closet.domain.users;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.domain.users.dto.ResetPasswordRequest;
import project.closet.domain.users.dto.SignInRequest;
import project.closet.domain.users.dto.SignInResponse;
import project.closet.global.config.security.JWTConfigProperties;
import project.closet.global.config.security.TokenType;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final JWTConfigProperties jwtProperties;

  // 로그 아웃
  @PostMapping("/sign-out")
  public ResponseEntity<?> logout(HttpServletRequest request) {

    authService.logout(request.getHeader("authorization"));
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  // 로그인
  @PostMapping("/sign-in")
  public ResponseEntity<String> login(
      @RequestBody @Valid SignInRequest request,
      HttpServletResponse response) {

    SignInResponse tokens = authService.login(request);
    response.setHeader(jwtProperties.header(), tokens.accessToken());
    response.addCookie(createCookie(TokenType.REFRESH.getTokenName(), tokens.refreshToken()));

    return ResponseEntity.status(HttpStatus.OK).body("Login Success");
  }

  @PostMapping("/reset-password")
  public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {

    authService.resetPassword(request);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<String> refresh(@RequestParam(name = "refreshToken") String refreshToken) {

    String newAccessToken = authService.reissueAccessToken(refreshToken);
    return ResponseEntity.status(HttpStatus.OK).body(newAccessToken);
  }

  @GetMapping("/me")
  public ResponseEntity<String> findAccessToken(HttpServletRequest request) {

    String accessToken = authService.getAccessToken(extractRefreshToken(request));
    return ResponseEntity.status(HttpStatus.OK).body(accessToken);
  }

  private Cookie createCookie(String name, String value) {
    Cookie cookie = new Cookie(name, value);
    cookie.setHttpOnly(true);
    cookie.setMaxAge((int) jwtProperties.refreshExpiration());
    return cookie;
  }

  private String extractRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (TokenType.REFRESH.getTokenName().equals(cookie.getName())) {
        return cookie.getValue();
      }
    }

    throw new BadCredentialsException("Missing or Invalid refresh-token");
  }
}
