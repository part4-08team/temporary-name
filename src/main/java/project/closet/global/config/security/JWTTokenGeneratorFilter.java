package project.closet.global.config.security;

import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;



public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

  private final JWTConfigProperties jwtProperties;
  private final JwtUtils jwtUtils;

  public JWTTokenGeneratorFilter(JWTConfigProperties jwtProperties, JwtUtils jwtUtils) {
    this.jwtProperties = jwtProperties;
    this.jwtUtils = jwtUtils;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // JWT Token 생성
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new BadCredentialsException("Authorization is failed");
    }

    try {

      // Access Token
      String token = Jwts.builder()
          .issuer("team-eight")
          .claim("username", authentication.getName())
          .claim("authorities", authentication.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .collect(Collectors.joining(",")))
          .issuedAt(new Date())
          .expiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
          .signWith(jwtUtils.getSecretKey()).compact();

      response.setHeader(jwtProperties.header(), "Bearer " + token);
      // redis -> user의 id key, value -> token
    } catch (Exception e) {
      throw new BadCredentialsException("Authorization is failed");
    }

    filterChain.doFilter(request, response);
  }


  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return !request.getServletPath().equals("/api/auth/sign-in");
  }
}
