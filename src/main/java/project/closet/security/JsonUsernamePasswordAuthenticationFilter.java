package project.closet.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import project.closet.dto.request.LoginRequest;

@RequiredArgsConstructor
public class JsonUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException {

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod()
            );
        }

        try {
            // Login Request 직렬화 -> JSON 을 Java type
            LoginRequest loginRequest =
                    objectMapper.readValue(request.getInputStream(), LoginRequest.class);

            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());

            setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);

        } catch (IOException e) {
            throw new AuthenticationServiceException("Request parsing failed", e);
        }
    }

    // 정적 생성자 메소드로 시큐리티 설정에서 간편하게 필터 생성 등록
    public static JsonUsernamePasswordAuthenticationFilter createDefault(
            ObjectMapper objectMapper,
            AuthenticationManager authenticationManager,
            SessionAuthenticationStrategy sessionAuthenticationStrategy
    ) {
        JsonUsernamePasswordAuthenticationFilter filter =
                new JsonUsernamePasswordAuthenticationFilter(objectMapper);
        // Login URI Custom
        filter.setRequiresAuthenticationRequestMatcher(SecurityMatchers.LOGIN);
        // Login 처리를 해주는 Manager(DaoAuthentication)
        filter.setAuthenticationManager(authenticationManager);
        // 로그인 처리 성공, 실패 핸들러
        filter.setAuthenticationSuccessHandler(new CustomLoginSuccessHandler(objectMapper));
        filter.setAuthenticationFailureHandler(new CustomLoginFailureHandler(objectMapper));
        filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        return filter;
    }

    public static class Configurer extends
            AbstractAuthenticationFilterConfigurer<HttpSecurity, Configurer, JsonUsernamePasswordAuthenticationFilter> {

        private final ObjectMapper objectMapper;

        public Configurer(ObjectMapper objectMapper) {
            super(new JsonUsernamePasswordAuthenticationFilter(objectMapper), SecurityMatchers.LOGIN_URL);
            this.objectMapper = objectMapper;
        }

        @Override
        protected RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
            return new AntPathRequestMatcher(loginProcessingUrl, HttpMethod.POST.name());
        }

        @Override
        public void init(HttpSecurity http) throws Exception {
            loginProcessingUrl(SecurityMatchers.LOGIN_URL);
            successHandler(new CustomLoginSuccessHandler(objectMapper));
            failureHandler(new CustomLoginFailureHandler(objectMapper));
        }
    }
}
