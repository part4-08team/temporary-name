package project.closet.dto.response;

import java.util.UUID;
import project.closet.weather.entity.SkyStatus;
import project.closet.weather.entity.Weather;

public record WeatherSummaryDto(
        UUID weatherId,
        SkyStatus skyStatus,
        PrecipitationDto precipitation,
        TemperatureDto temperature
) {

    public static WeatherSummaryDto from (Weather weather) {
        return new WeatherSummaryDto(
                weather.getId(),
                weather.getSkyStatus(),
               PrecipitationDto.from(weather),
                new TemperatureDto(
                        weather.getCurrentTemperature(),
                        0.0,
                        weather.getMinTemperature(),
                        weather.getMaxTemperature()
                )
        );
    }
}
