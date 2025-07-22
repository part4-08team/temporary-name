package project.closet.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.web.servlet.HandlerInterceptor;

public class MDCLoggingInterceptor implements HandlerInterceptor {

    public static final String REQUEST_ID = "requestId";
    public static final String REQUEST_METHOD = "requestMethod";
    public static final String REQUEST_URI = "requestUri";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestId = UUID.randomUUID().toString().replaceAll("-", "");

        MDC.put(REQUEST_ID, requestId);
        MDC.put(REQUEST_METHOD, request.getMethod());
        MDC.put(REQUEST_URI, request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        MDC.clear();
    }
}
