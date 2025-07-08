package project.closet.dto.response;

import project.closet.weather.entity.PrecipitationType;

public record PrecipitationDto(
        PrecipitationType type,
        Double amount,
        Double probability
) {

}
