package project.closet.domain.users.user.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import project.closet.common.util.TimeConverter;
import project.closet.domain.users.auth.OAuthProvider;
import project.closet.domain.users.user.User;
import project.closet.domain.users.user.UserRole;

public record UserDto(
    UUID id,
    // 포멧 나중에
    LocalDateTime createdAt,
    String email,
    String name,
    UserRole role,
    Set<OAuthProvider> linkedOAuthProviders,
    boolean locked
) {

  public static UserDto from(User user) {

    return new UserDto(
        user.getId(),
        TimeConverter.toLocalDateTime(user.getCreatedAt()),
        user.getEmail(),
        user.getProfile().getName(),
        user.getRole(),
        null,
        user.isLocked()
    );
  }
}
