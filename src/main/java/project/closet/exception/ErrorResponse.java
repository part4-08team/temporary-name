package project.closet.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public class ErrorResponse {

    private final String exceptionName;
    private final String message;
    private final Map<String, Object> details;
    private final int status;

    public ErrorResponse(String exceptionName, String message, Map<String, Object> details, int status) {
        this.exceptionName = exceptionName;
        this.message = message;
        this.details = details != null ? details : new HashMap<>();
        this.status = status;
    }

    public ErrorResponse(ClosetException exception, int status) {
        this(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                exception.getDetails(),
                status
        );
    }


    public ErrorResponse(Exception exception, int status) {
        this(
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                new HashMap<>(),
                status
        );
    }
}
