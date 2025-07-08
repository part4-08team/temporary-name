package project.closet.dto.response;

import project.closet.weather.entity.AsWord;

public record WindSpeedDto(
        Double speed,
        AsWord asWord
) {

}
