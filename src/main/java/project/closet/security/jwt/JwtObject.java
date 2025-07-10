package project.closet.security.jwt;

import java.time.Instant;
import java.util.UUID;
import project.closet.dto.response.UserDto;
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

    public static JwtObject of(
            Instant issueTime,
            Instant expirationTime,
            UserDto userDto,
            String token
    ) {
        return new JwtObject(
                issueTime,
                expirationTime,
                userDto.userId(),
                userDto.name(),
                userDto.email(),
                userDto.role(),
                token
        );
    }

    public UserDto toUserDto() {
        return new UserDto(
                userId,
                name,
                email,
                role
        );
    }

    public boolean isExpired() {
        return expirationTime.isBefore(Instant.now());
    }
}
