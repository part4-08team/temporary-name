package project.closet.exception.weather;

import project.closet.exception.ClosetException;
import project.closet.exception.ErrorCode;

public class WeatherException extends ClosetException {

    public WeatherException(ErrorCode errorCode) {
        super(errorCode);
    }

    public WeatherException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
