package project.closet.domain.users.user.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import project.closet.common.dto.Location;
import project.closet.domain.users.user.Gender;
import project.closet.domain.users.user.TemperatureSensitivity;

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
