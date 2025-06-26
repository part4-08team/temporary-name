package project.closet.domain.users.user.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import project.closet.domain.users.util.UrlValidatorUtil;

public record ProfileUpdateWithImageUrlRequest(

    @Valid
    ProfileUpdateRequest profileUpdateRequest,

    @NotBlank(message = "메시지를 입력해주세요")
    String imageUrl
) {

  public ProfileUpdateWithImageUrlRequest {

    if (!UrlValidatorUtil.isValidUrl(imageUrl)) {
      throw new IllegalArgumentException("유효하지 않은 imageUrl 입니다.");
    }
  }
}
