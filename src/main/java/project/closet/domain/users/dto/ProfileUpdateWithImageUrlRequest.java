package project.closet.domain.users.dto;

import jakarta.validation.Valid;
import project.closet.domain.users.util.UrlValidatorUtil;

public record ProfileUpdateWithImageUrlRequest(

    @Valid
    ProfileUpdateRequest profileUpdateRequest,

    String imageUrl
) {

  public ProfileUpdateWithImageUrlRequest {
    if (imageUrl != null && !UrlValidatorUtil.isValidUrl(imageUrl)) {
      throw new IllegalArgumentException("유효하지 않은 imageUrl 입니다.");
    }
  }
}
