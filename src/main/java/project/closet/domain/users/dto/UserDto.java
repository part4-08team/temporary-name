package project.closet.domain.users.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import project.closet.common.util.TimeConverter;
import project.closet.domain.users.OAuthProvider;
import project.closet.domain.users.User;
import project.closet.domain.users.User.UserRole;

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
    // todo : OAuth 기능 추가 시 linkedOAuthProviders 필요
    return new UserDto(
        user.getId(),
        TimeConverter.toLocalDateTime(user.getCreatedAt()),
        user.getEmail(),
        user.getProfile().getName(),
        user.getRole(),
        new HashSet<>(),
        user.isLocked()
    );
  }
}
