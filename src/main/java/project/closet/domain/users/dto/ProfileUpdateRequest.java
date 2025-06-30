package project.closet.domain.users.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import project.closet.common.dto.Location;
import project.closet.domain.users.Profile.TemperatureSensitivity;
import project.closet.domain.users.User.Gender;

public record ProfileUpdateRequest(
    @NotBlank(message = "이름을 입력해주세요.")
    String name,

    //@NotBlank(message = "성별을 입력해주세요.")
    Gender gender,

    //@NotBlank(message = "생년월일을 입력해주세요.")
    //@DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate birthDate,

    //@NotBlank(message = "위치를 입력해주세요.")
    Location location,

    //@NotBlank(message = "온도 민감도를 입력해주세요.")
    TemperatureSensitivity temperatureSensitivity
) {

}
