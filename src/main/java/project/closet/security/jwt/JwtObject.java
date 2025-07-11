package project.closet.security.jwt;

import java.time.Instant;
import java.util.UUID;
import project.closet.user.entity.Role;

public record JwtObject(
        Instant issueTime,
        Instant expirationTime,
        UUID userId,
        String name,
        String email,
        Role role,
        String token
) {

    public boolean isExpired() {
        return expirationTime.isBefore(Instant.now());
    }
}
