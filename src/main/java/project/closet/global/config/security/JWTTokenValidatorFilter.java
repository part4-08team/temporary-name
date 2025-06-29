package project.closet.global.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
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

public class JWTTokenValidatorFilter extends OncePerRequestFilter {

  private final JWTConfigProperties jwtProperties;
  private final JwtUtils jwtUtils;

  public JWTTokenValidatorFilter(JWTConfigProperties jwtProperties, JwtUtils jwtUtils) {
    this.jwtProperties = jwtProperties;
    this.jwtUtils = jwtUtils;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String jwt = request.getHeader(jwtProperties.header());

    if (jwt == null || !jwt.startsWith("Bearer ")) {
      throw new BadCredentialsException("Missing or invalid Authorization header");
    }

    try {
      String username = jwtUtils.getUsername(jwt);
      List<GrantedAuthority> authorities = jwtUtils.getAuthorities(jwt);
      Authentication authentication = new UsernamePasswordAuthenticationToken(
          username, null, authorities);

      SecurityContextHolder.getContext().setAuthentication(authentication);

    } catch (Exception e) {
      throw new BadCredentialsException("Invalid token");
    }

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getServletPath().equals("/api/auth/sign-in");
  }
}
