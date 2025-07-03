package project.closet.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record ProfileDto(
        UUID userId,
        String name,
        String gender,
        LocalDate birthDate,
        String location,
        Integer temperatureSensitivity,
        String profileImageUrl
) {
}
