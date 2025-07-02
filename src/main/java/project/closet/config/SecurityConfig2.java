package project.closet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.closet.global.config.security.SecurityMatchers;
import project.closet.security.JsonUsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig2 {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            ObjectMapper objectMapper
    ) throws Exception {
        http
                // filter 검사할 URL
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SecurityMatchers.PUBLIC_MATCHERS).permitAll()
                        .anyRequest().authenticated()
                )
                .logout(AbstractHttpConfigurer::disable)
                // 로그인 필터 등록
                .addFilterAt(
                        JsonUsernamePasswordAuthenticationFilter.createDefault(objectMapper),
                        UsernamePasswordAuthenticationFilter.class
                )
        ;

        return http.build();
    }

    @Bean
    public String debugFilterChain(SecurityFilterChain filterChain) {
        log.debug("Debug Filter Chain...");
        int filterSize = filterChain.getFilters().size();
        IntStream.range(0, filterSize)
                .forEach(idx -> {
                    log.debug("[{}/{}] {}", idx + 1, filterSize, filterChain.getFilters().get(idx));
                });
        return "debugFilterChain";
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
