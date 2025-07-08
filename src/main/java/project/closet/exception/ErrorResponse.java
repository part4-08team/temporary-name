package project.closet.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final String exceptionName;
    private final String message;
    private final Map<String, Object> details;
    private final int status;


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
