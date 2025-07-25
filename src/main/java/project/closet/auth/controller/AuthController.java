package project.closet.auth.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.closet.auth.controller.api.AuthApi;
import project.closet.auth.service.AuthService;
import project.closet.dto.request.ResetPasswordRequest;
import project.closet.exception.ErrorCode;
import project.closet.security.jwt.JwtException;
import project.closet.security.jwt.JwtService;
import project.closet.security.jwt.JwtSession;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

    private final AuthService authService;
    private final JwtService jwtService;

    @Override
    @GetMapping("/csrf-token")
    public ResponseEntity<CsrfToken> getCsrfToken(CsrfToken csrfToken) {
        log.debug("CSRF 토큰 요청");
        return ResponseEntity.status(HttpStatus.OK).body(csrfToken);
    }

    @Override
    @GetMapping("/me")
    public ResponseEntity<String> me(
            @CookieValue(value = JwtService.REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken
    ) {
        log.info("내 정보 조회 요청");
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new JwtException(ErrorCode.TOKEN_NOT_FOUND);
        }

        JwtSession jwtSession = jwtService.getJwtSession(refreshToken);
        return ResponseEntity.ok(jwtSession.getAccessToken());
    }

    @PostMapping("refresh")
    public ResponseEntity<String> refresh(
            @CookieValue(JwtService.REFRESH_TOKEN_COOKIE_NAME) String refreshToken,
            HttpServletResponse response
    ) {
        log.info("토큰 재발급 요청");
        JwtSession jwtSession = jwtService.refreshJwtSession(refreshToken);

        Cookie refreshTokenCookie = new Cookie(JwtService.REFRESH_TOKEN_COOKIE_NAME,
                jwtSession.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(jwtSession.getAccessToken());
    }

    @PostMapping("reset-password")
    @Override
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequest resetPasswordRequest
    ) {
        authService.resetPassword(resetPasswordRequest.email());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
