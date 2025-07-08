package project.closet.exception.weather;

import java.util.UUID;
import project.closet.exception.ErrorCode;

public class WeatherNotFoundException extends WeatherException {

    public WeatherNotFoundException() {
        super(ErrorCode.WEATHER_NOT_FOUND);
    }

    public static WeatherNotFoundException withId(UUID weatherId) {
        WeatherNotFoundException exception = new WeatherNotFoundException();
        exception.addDetail("weatherId", weatherId);
        return exception;
    }
}
