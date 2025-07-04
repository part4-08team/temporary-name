package project.closet.dto.request;

import java.time.LocalDate;
import project.closet.dto.response.WeatherAPILocation;
import project.closet.user.entity.Gender;

public record ProfileUpdateRequest(
        String name,
        Gender gender,
        LocalDate birthDate,
        WeatherAPILocation location,
        Integer temperatureSensitivity
) {

}
