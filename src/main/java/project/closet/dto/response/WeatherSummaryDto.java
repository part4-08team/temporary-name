package project.closet.dto.response;

import java.util.UUID;
import project.closet.weather.entity.SkyStatus;

public record WeatherSummaryDto(
        UUID weatherId,
        SkyStatus skyStatus,
        PrecipitationDto precipitation,
        TemperatureDto temperature
) {

}
