package project.closet.dto.request;

import java.time.LocalDate;
import project.closet.dto.response.WeatherAPILocation;

public record ProfileUpdateRequest(
        String name,
        String gender,
        LocalDate birthDate,
        WeatherAPILocation location,
        Integer temperatureSensitivity
) {

}
