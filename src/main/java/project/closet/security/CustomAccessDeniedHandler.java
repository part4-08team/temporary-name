package project.closet.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import project.closet.exception.ErrorResponse;

@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {
        log.warn("Access denied: user={} attempted {} {} — {}",
                request.getUserPrincipal(),
                request.getMethod(),
                request.getRequestURI(),
                accessDeniedException.getMessage()
        );

        ErrorResponse errorResponse =
                new ErrorResponse(
                        accessDeniedException.getClass().getSimpleName(),
                        "권한이 없습니다.",
                        Map.of(
                                "path", request.getRequestURI(),
                                "method", request.getMethod(),
                                "timestamp", Instant.now().toString()
                        ),
                        HttpServletResponse.SC_FORBIDDEN
                );

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
