package project.closet.global.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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


  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return !request.getServletPath().equals("/api/auth/sign-in");
  }
}
