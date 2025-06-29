package project.closet.global.config.security;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Validated
public record JWTConfigProperties(
    @NotBlank(message = "jwt.secret is required")
    String secret,

    @NotBlank(message = "jwt.header is required")
    String header,

    @Min(1)
    long expiration,

    @Min(86400) // 최소 1DAY 이상
    long refreshExpiration) {
}
