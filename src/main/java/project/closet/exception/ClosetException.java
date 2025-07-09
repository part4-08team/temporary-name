package project.closet.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

@Getter
public abstract class ClosetException extends RuntimeException {

    private final Instant timestamp;
    private final ErrorCode errorCode;
    private final Map<String, Object> details = new HashMap<>();

    protected ClosetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
    }

    protected ClosetException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.timestamp = Instant.now();
        this.errorCode = errorCode;
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }

}
