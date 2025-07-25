package project.closet.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String name = ex.getName();
        String value = ex.getValue() != null ? ex.getValue().toString() : "null";
        String message = String.format("잘못된 요청 파라미터입니다: '%s' 값 '%s'는 유효하지 않습니다.", name, value);
        log.warn("파라미터 타입 불일치: parameter='{}', invalidValue='{}'", name, value);

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

    @ExceptionHandler(ClosetException.class)
    public ResponseEntity<ErrorResponse> handleClosetException(ClosetException ex) {
        log.warn("비즈니스 예외 발생: code={}, message={}", ex.getErrorCode(), ex.getMessage());

        HttpStatus status = mapToHttpStatus(ex.getErrorCode());
        ErrorResponse response = new ErrorResponse(
                ex.getClass().getSimpleName(),
                ex.getMessage(),
                ex.getDetails(),
                status.value()
        );
        return ResponseEntity.status(status).body(response);
    }

    //모든 예상하지 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("알 수 없는 예외 발생", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException exception
    ) {
        log.error("요청 유효성 검사 실패: {}", exception.getMessage());

        Map<String, Object> validationErrors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponse response = new ErrorResponse(
                "VALIDATION_ERROR",
                "요청 데이터 유효성 검사에 실패했습니다",
                validationErrors,
                HttpStatus.BAD_REQUEST.value()
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // SSE stream 의 타임아웃은 무시만 하고 아무것도 리턴하지 않습니다.
    @ExceptionHandler(AsyncRequestTimeoutException.class)
    public void handleSseTimeout(AsyncRequestTimeoutException ex) {
        // no-op: suppress the timeout exception, so GlobalExceptionHandler 에게 안 넘어감
        log.debug("SSE 타임아웃 발생(무시): {}", ex.getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Void> handleNoResource(NoResourceFoundException ex) {
        log.debug("No static resource: {}", ex.getResourcePath());
        return ResponseEntity.notFound().build();
    }

    private HttpStatus mapToHttpStatus(ErrorCode code) {
        return switch (code) {
            case DM_NOT_FOUND, FEED_NOT_FOUND, USER_NOT_FOUND, ATTRIBUTE_DEFINITION_NOT_FOUND,
                 CLOTHES_NOT_FOUND, DUPLICATE_USER, FOLLOW_NOT_FOUND, WEATHER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case INVALID_REQUEST, ATTRIBUTE_DEFINITION_DUPLICATE, FEED_ALREADY_LIKE_EXISTS,
                 SELF_FOLLOW_NOT_ALLOWED,EXTRACTION_FAILED, UNSUPPORTED_SHOP -> HttpStatus.BAD_REQUEST;
            case INVALID_TOKEN, TOKEN_NOT_FOUND, INVALID_TOKEN_SECRET -> HttpStatus.UNAUTHORIZED;
            case INTERNAL_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
