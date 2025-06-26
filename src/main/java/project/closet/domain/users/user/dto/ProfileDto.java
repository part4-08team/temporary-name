package project.closet.domain.users.user.dto;

import java.time.LocalDate;
import java.util.UUID;
import project.closet.common.dto.Location;
import project.closet.domain.users.user.Gender;
import project.closet.domain.users.user.Profile;
import project.closet.domain.users.user.TemperatureSensitivity;
import project.closet.domain.users.util.UrlValidatorUtil;

public record ProfileDto(
    UUID userId,
    String name,
    Gender gender,
    LocalDate birthDate,
    Location location,
    TemperatureSensitivity temperatureSensitivity,
    String profileImageUrl
) {

  public ProfileDto {
    if (!UrlValidatorUtil.isValidUrl(profileImageUrl)) {
      throw new IllegalArgumentException("profileImageUrl must be a valid URL");
    }
  }

  public static ProfileDto of(UUID userId, Profile profile) {
    return new ProfileDto(
        userId,
        profile.getName(),
        profile.getGender(),
        profile.getBirthDate(),
        profile.getLocation(),
        profile.getTemperatureSensitivity(),
        profile.getProfileImageUrl()
    );
  }
}
