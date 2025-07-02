package project.closet.config;

import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import project.closet.global.config.security.SecurityMatchers;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig2 {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // filter 검사할 URL
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SecurityMatchers.PUBLIC_MATCHERS).permitAll()
                        .anyRequest().authenticated()
                )
                .logout(AbstractHttpConfigurer::disable)
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
}
