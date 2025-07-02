package project.closet.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public abstract class ClosetException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Map<String, Object> details = new HashMap<>();

    protected ClosetException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    protected ClosetException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }

    public void addDetail(String key, Object value) {
        this.details.put(key, value);
    }
}