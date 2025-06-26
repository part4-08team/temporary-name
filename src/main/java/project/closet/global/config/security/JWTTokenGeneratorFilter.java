package project.closet.global.config.security;

import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import javax.crypto.SecretKey;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;



public class JWTTokenGeneratorFilter extends OncePerRequestFilter {

  private final JWTConfigProperties jwtProperties;

  public JWTTokenGeneratorFilter(JWTConfigProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // JWT Token 생성
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null) {
      throw new BadCredentialsException("Authorization is failed");
    }

    long expiration = System.currentTimeMillis() + jwtProperties.expiration();

    try {
      SecretKey secretKey = Keys.hmacShaKeyFor(jwtProperties.secret().getBytes());
      // jwtbuilder


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
