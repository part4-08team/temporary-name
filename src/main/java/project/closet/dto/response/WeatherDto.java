package project.closet.dto.response;

import jakarta.annotation.Nullable;
import java.time.Instant;
import java.util.UUID;
import project.closet.weather.entity.SkyStatus;
import project.closet.weather.entity.Weather;

public record WeatherDto(
        UUID id,
        Instant forecastedAt,
        Instant forecastAt,
        WeatherAPILocation location,
        SkyStatus skyStatus,
        Precipitation precipitation,
        Humidity humidity,
        TemperatureDto temperature,
        WindSpeedDto windSpeed
) {
    public static WeatherDto from(Weather current, @Nullable Weather previousDay) {
        double temperatureDiff = 0.0;
        double humidityDiff = 0.0;

        if (previousDay != null) {
            temperatureDiff = current.getCurrentTemperature() - previousDay.getCurrentTemperature();
            humidityDiff = current.getHumidity() - previousDay.getHumidity();
        }

        return new WeatherDto(
                current.getId(),
                current.getForecastedAt(),
                current.getForecastAt(),
                new WeatherAPILocation(
                        0.0, 0.0,
                        current.getX(),
                        current.getY(),
                        null
                ),
                current.getSkyStatus(),
                new Precipitation(
                        current.getPrecipitationType(),
                        current.getAmount(),
                        current.getProbability() / 100.0
                ),
                new Humidity(
                        current.getHumidity(),
                        humidityDiff
                ),
                new TemperatureDto(
                        current.getCurrentTemperature(),
                        temperatureDiff,
                        current.getMinTemperature(),
                        current.getMaxTemperature()
                ),
                new WindSpeedDto(
                        current.getWindSpeed(),
                        current.getAsWord()
                )
        );
    }
}
