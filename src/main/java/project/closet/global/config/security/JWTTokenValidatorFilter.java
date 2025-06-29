package project.closet.global.config.security;

import io.jsonwebtoken.ExpiredJwtException;
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

    String accessToken = request.getHeader(jwtProperties.header());

    if (accessToken == null || !accessToken.startsWith("Bearer ")) {
      throw new BadCredentialsException("Missing or invalid Authorization header");
    }

    try {
      jwtUtils.validateTokenExpiration(accessToken);
    } catch (ExpiredJwtException e){
      // 프론트 코드 나중에 찾아보기 - reissue 엔드포인트로 가도록
      response.getWriter().write("Access-Token is expired");
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }




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

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return request.getServletPath().equals("/api/auth/sign-in");
  }
}
