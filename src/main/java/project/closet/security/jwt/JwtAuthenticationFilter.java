package project.closet.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import project.closet.exception.ErrorCode;
import project.closet.exception.ErrorResponse;
import project.closet.security.ClosetUserDetails;
import project.closet.security.SecurityMatchers;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> optionalAccessToken = resolveToken(request);
        if (optionalAccessToken.isPresent() && !isPermitAll(request)) {
            String accessToken = optionalAccessToken.get();
            if (jwtService.validate(accessToken)) {
                JwtObject jwtObject = jwtService.parse(accessToken);
                ClosetUserDetails userDetails = ClosetUserDetails.from(jwtObject);
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(auth);

                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                ErrorResponse errorResponse =
                        new ErrorResponse(
                                new JwtException(
                                        ErrorCode.INVALID_TOKEN,
                                        Map.of("accessToken", accessToken)
                                ),
                                HttpServletResponse.SC_UNAUTHORIZED
                        );
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        String prefix = "Bearer ";
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION))
                .map(value -> {
                    if (value.startsWith(prefix)) {
                        return value.substring(prefix.length());
                    } else {
                        return null;
                    }
                });
    }

    private boolean isPermitAll(HttpServletRequest request) {
        return Arrays.stream(SecurityMatchers.PUBLIC_MATCHERS)
                .anyMatch(requestMatcher -> requestMatcher.matches(request));
    }
}
