package project.closet.domain.users.dto;

import java.util.UUID;
import project.closet.domain.users.User.UserRole;

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

    // 기본값 설정 뭐로 하지
  }
}
