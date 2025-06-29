package project.closet.global.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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

  public String getUsername(String token) {
    return getPayload(token).get("username", String.class);
  }

  public UUID getUserId(String token) {
    return UUID.fromString(getPayload(token).get("userId", String.class));
  }

  public List<GrantedAuthority> getAuthorities(String token) {
    String authorities = getPayload(token).get("authorities", String.class);
    return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
  }

  public boolean isExpired(String token) {
    return getPayload(token).getExpiration().before(new Date());
  }

  public SecretKey getSecretKey() {
    return Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
  }

  private Claims getPayload(String token) {
    return parser.parseSignedClaims(token).getPayload();
  }
}
