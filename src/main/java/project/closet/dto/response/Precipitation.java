package project.closet.dto.response;

import project.closet.weather.entity.PrecipitationType;

public record Precipitation(
        PrecipitationType type,
        Double amount,
        Double probability
) {

}
