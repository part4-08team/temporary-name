package project.closet.security.jwt;

import java.time.Instant;
import project.closet.dto.response.UserDto;

public record JwtObject(
        Instant issueTime,
        Instant expirationTime,
        UserDto userDto,
        String token
) {

    public boolean isExpired() {
        return expirationTime.isBefore(Instant.now());
    }
}
