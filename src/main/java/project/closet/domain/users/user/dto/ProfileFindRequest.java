package project.closet.domain.users.user.dto;

import java.util.Objects;
import java.util.UUID;
import project.closet.domain.users.user.UserRole;

public record ProfileFindRequest(
    String cursor,
    UUID idAfter,
    int limit,
    String sortBy,
    String sortDirection,
    String emailLike,
    UserRole roleEqual,
    boolean locked
) {

  public ProfileFindRequest {

    if (limit < 0) {
      throw new IllegalArgumentException("limit must be greater than 0");
    }
    Objects.requireNonNull(sortBy, "sortBy must not be null");
    Objects.requireNonNull(sortDirection, "sortDirection must not be null");

    // 기본값 설정 뭐로 하지
  }
}
