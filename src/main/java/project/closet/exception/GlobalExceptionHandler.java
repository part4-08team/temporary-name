package project.closet.exception;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        String message = String.format("잘못된 요청 파라미터입니다: '%s' 값 '%s'는 유효하지 않습니다.", name, value);

        Map<String, Object> details = new HashMap<>();
        details.put("parameter", name);
        details.put("invalidValue", value);

        ErrorResponse response = new ErrorResponse(
                ex.getClass().getSimpleName(),
                message,
                details,
                HttpStatus.BAD_REQUEST.value()
        );
        return ResponseEntity.badRequest().body(response);
    }

    //도메인에서 명시적으로 발생시킨 사용자 정의 예외
    @ExceptionHandler(ClosetException.class)
    public ResponseEntity<ErrorResponse> handleClosetException(ClosetException ex) {
        HttpStatus status = mapToHttpStatus(ex.getErrorCode());
        ErrorResponse response = new ErrorResponse(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex.getDetails(),
                status.value()
        );
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(
            AuthorizationDeniedException exception) {
        ErrorResponse errorResponse = new ErrorResponse(exception, HttpStatus.FORBIDDEN.value());
        return ResponseEntity
                .status(errorResponse.getStatus())
                .body(errorResponse);
    }

    //모든 예상하지 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    private HttpStatus mapToHttpStatus(ErrorCode code) {
        return switch (code) {
            case DM_NOT_FOUND, FEED_NOT_FOUND, USER_NOT_FOUND, ATTRIBUTE_DEFINITION_NOT_FOUND ->
                    HttpStatus.NOT_FOUND;
            case INVALID_REQUEST, ATTRIBUTE_DEFINITION_DUPLICATE, FEED_ALREADY_LIKE_EXISTS -> HttpStatus.BAD_REQUEST;
            case INVALID_TOKEN, TOKEN_NOT_FOUND, INVALID_TOKEN_SECRET -> HttpStatus.UNAUTHORIZED;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
