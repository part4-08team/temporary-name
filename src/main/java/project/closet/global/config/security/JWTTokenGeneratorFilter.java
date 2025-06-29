//package project.closet.global.config.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.List;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//
//
//public class JWTTokenGeneratorFilter extends OncePerRequestFilter {
//
//  private final JWTConfigProperties jwtProperties;
//  private final JwtUtils jwtUtils;
//
//  public JWTTokenGeneratorFilter(JWTConfigProperties jwtProperties, JwtUtils jwtUtils) {
//    this.jwtProperties = jwtProperties;
//    this.jwtUtils = jwtUtils;
//  }
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
//      FilterChain filterChain) throws ServletException, IOException {
//    // JWT Token 생성
//    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//    if (authentication == null) {
//      throw new BadCredentialsException("Authorization is failed");
//    }
//
//    try {
//
//      ClosetUserDetails userDetails = (ClosetUserDetails) authentication.getPrincipal();
//
//      @SuppressWarnings("unchecked")
//      String accessToken = jwtUtils.createJwtToken(TokenType.ACCESS,userDetails.getUserId(),
//          userDetails.getUsername(), (List<GrantedAuthority>) authentication.getAuthorities());
//
//      @SuppressWarnings("unchecked")
//      String refreshToken = jwtUtils.createJwtToken(TokenType.REFRESH, userDetails.getUserId(),
//          userDetails.getUsername(), (List<GrantedAuthority>) authentication.getAuthorities());
//
//      response.setHeader(jwtProperties.header(), "Bearer " + accessToken);
//      response.addCookie(createCookie(TokenType.REFRESH.name(), refreshToken));
//      response.setStatus(HttpServletResponse.SC_OK);
//
//      // redis -> user의 id key, value -> token
//    } catch (Exception e) {
//      throw new BadCredentialsException("Authorization is failed");
//    }
//
//    filterChain.doFilter(request, response);
//  }
//
//
//  @Override
//  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
//    return !request.getServletPath().equals("/api/auth/sign-in");
//  }
//
//  private Cookie createCookie(String name, String value) {
//    Cookie cookie = new Cookie(name, value);
//    cookie.setHttpOnly(true);
//    cookie.setMaxAge((int) jwtProperties.refreshExpiration());
//    return cookie;
//  }
//}
