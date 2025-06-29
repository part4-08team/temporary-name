package project.closet.global.config.security;

import java.time.Instant;

public record BlackListInfo(
    String token,
    Instant expirationAt) {
}
