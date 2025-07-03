package project.closet.dto.response;

import java.time.LocalDate;
import java.util.UUID;
import project.closet.user.entity.Gender;
import project.closet.user.entity.Profile;
import project.closet.user.entity.User;

public record ProfileDto(
        UUID userId,
        String name,
        Gender gender,
        LocalDate birthDate,
        WeatherAPILocation location,
        Integer temperatureSensitivity,
        String profileImageUrl
) {

    public static ProfileDto of(
            User user,
            WeatherAPILocation location,
            Profile profile
    ) {
        return new ProfileDto(
                user.getId(),
                user.getName(),
                profile.getGender(),
                profile.getBirthDate(),
                location,
                profile.getTemperatureSensitivity(),
                profile.getProfileImageUrl()
        );
    }
}
