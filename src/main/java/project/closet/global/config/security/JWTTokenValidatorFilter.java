package project.closet.global.config.security;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import project.closet.global.config.redis.RedisRepository;

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

  private final JWTConfigProperties jwtProperties;
  private final JwtUtils jwtUtils;
  private final RedisRepository redisRepository;

  public JWTTokenValidatorFilter(JWTConfigProperties jwtProperties, JwtUtils jwtUtils,
      RedisRepository redisRepository) {
    this.jwtProperties = jwtProperties;
    this.jwtUtils = jwtUtils;
    this.redisRepository = redisRepository;
  }

  /**
   * 계정 잠금 확인 必 (강제 로그아웃되야 함)
   * But 매번 JWT 검증 시 DB 접근은 너무 TOO Much => Redis에 locked된 유저 확인
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String accessToken = request.getHeader(jwtProperties.header());
    if (accessToken == null || !accessToken.startsWith("Bearer ")) {
      throw new BadCredentialsException("Missing or invalid Authorization header");
    }

    validateTokenExpiration(accessToken);
    validateTokenType(TokenType.ACCESS, accessToken);
    validateAccessTokenInRedis(accessToken);
    /**
     * todo : 블랙리스트인지 확인
     */

    try {
      String username = jwtUtils.getUsername(accessToken);
      List<GrantedAuthority> authorities = jwtUtils.getAuthorities(accessToken);
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          username, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception e) {
      throw new BadCredentialsException("Invalid token");
    }

    filterChain.doFilter(request, response);
  }


  /**
   * null : 만료 or 로그아웃 시킨 것
   * not equal : 다른 로그인으로 인해 강제 로그아웃
   */
  private void validateAccessTokenInRedis(String accessToken) {
    String redisAccessToken = (String) redisRepository.findByUserId(jwtUtils.getUserId(accessToken));
    if (redisAccessToken == null || !redisAccessToken.equals(accessToken)) {
      throw new BadCredentialsException("Invalid accesstoken");
    }
  }

  private String extractRefreshToken(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    for (Cookie cookie : cookies) {
      if (TokenType.ACCESS.name().equals(cookie.getName())) {
        return cookie.getValue();
      }
    }

    throw new BadCredentialsException("Missing refresh token");
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {

    return request.getServletPath()
        .equals("/api/auth/sign-in") || request.getServletPath().equals("/api/auth/reissue");
  }

  /**
   * todo : 프론트 코드 나중에 찾아보기 - reissue 엔드포인트로 가도록
   */
  private void validateTokenExpiration(String accessToken) throws BadCredentialsException {
    try {
      jwtUtils.validateTokenExpiration(accessToken);
    } catch (ExpiredJwtException e) {
      throw new BadCredentialsException("Access-Token is expired");
    }
  }

  private void validateTokenType(TokenType type, String token) throws BadCredentialsException {
    String tokenType = jwtUtils.getTokenType(token);
    if (!type.name().equals(tokenType)) {
      throw new BadCredentialsException("Invalid token type");
    }
  }
}
