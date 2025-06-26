package project.closet.domain.users.user.dto;

import java.time.LocalDate;
import project.closet.common.dto.Location;
import project.closet.domain.users.user.Gender;
import project.closet.domain.users.user.TemperatureSensitivity;

public record ProfileUpdateRequest(
    String name,
    Gender gender,
    LocalDate birthDate,
    Location location,
    TemperatureSensitivity temperatureSensitivity
) {

}
