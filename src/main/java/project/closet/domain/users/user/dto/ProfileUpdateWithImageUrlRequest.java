package project.closet.domain.users.user.dto;

import jakarta.validation.Valid;
import project.closet.domain.users.util.UrlValidatorUtil;

public record ProfileUpdateWithImageUrlRequest(

    @Valid
    ProfileUpdateRequest profileUpdateRequest,

    String imageUrl
) {

  public ProfileUpdateWithImageUrlRequest {

    boolean isValidUrl = UrlValidatorUtil.isValidUrl(imageUrl);

    if (!isValidUrl) {
      throw new IllegalArgumentException("유효하지 않은 imageUrl 입니다.");
    }
  }
}
