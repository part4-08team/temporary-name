package project.closet.global.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;

@Component
public class JwtUtils {

  private final JWTConfigProperties properties;
  private final JwtParser parser;

  public JwtUtils(JWTConfigProperties properties) {
    this.properties = properties;
    parser = Jwts.parser().verifyWith(getSecretKey()).build();
  }

  public UUID getUserId(String token) {
    return UUID.fromString(getPayload(token).get("userId", String.class));
  }

  public String getUsername(String token) {
    return getPayload(token).get("username", String.class);
  }

  public List<GrantedAuthority> getAuthorities(String token) {
    String authorities = getPayload(token).get("authorities", String.class);
    return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
  }

  public void validateTokenExpiration(String token) {
    parser.parseSignedClaims(token);
  }

  public SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
  }

  public String getTokenType(String token) {
    return getPayload(token).get("type", String.class);
  }

  public String createJwtToken(TokenType category, UUID userId, String username,
      List<GrantedAuthority> authorities) {

    return Jwts.builder()
        .claim("type", category.name())
        .claim("userId", userId.toString())
        .claim("username", username)
        .claim("authorities", joiningAuthorities(authorities))
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + properties.expiration()))
        .signWith(getSecretKey())
        .compact();
  }

  private String joiningAuthorities(List<GrantedAuthority> authorities) {
    return authorities.stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));
  }

  private Claims getPayload(String token) {
    return parser.parseSignedClaims(token).getPayload();
  }
}
