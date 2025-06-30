package project.closet.domain.users;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    http.cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
//         @Override
//         public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
//           CorsConfiguration config = new CorsConfiguration();
//           config.setAllowedOriginPatterns(List.of(ALLOWED_ORIGINS));
//           config.setAllowedOrigins(Collections.singletonList("*"));
//           config.setAllowedHeaders(Collections.singletonList("*"));
//           config.setExposedHeaders(Collections.singletonList("Authorization"));
//           config.setAllowCredentials(true);
//           config.setMaxAge(3600L);
//           return config;
//         }
//       }
//    ));
    http.csrf(AbstractHttpConfigurer::disable);

    http.authorizeHttpRequests(request -> request
        .requestMatchers("/api/auth/sign-in").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
        .requestMatchers(HttpMethod.PATCH, "/api/users/*/password")
        .hasAnyRole("TEMP", "USER") // 임시비밀번호 로그인 시 URL
        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PATCH, "/api/users/*/role").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PATCH, "/api/users/*/lock").hasRole("ADMIN")
        .requestMatchers("/api/*").hasRole("USER")
    );

    return http.build();
  }

}
