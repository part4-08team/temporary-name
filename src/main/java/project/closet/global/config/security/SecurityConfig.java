package project.closet.global.config.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
public class SecurityConfig {

  // 임시 - 나중에 프론트 Origin으로 변경
  private static final String[] ALLOWED_ORIGINS = {
      "http://localhost:5173",
      "https://project.sb.sprinnt.learn.codeit.kr", // prod용 : front request에 이렇게
  };



  /**
   어드민 기능 - 초기화, 권환 관리, 계정 잠금
   회원 가입
   로그인 : jwt 기반
   비밀본호 초기화
   */

  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    // http.cors(corsConfig -> corsConfig.disable());  // cors 불확인 (임시)
    http.cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
          @Override
          public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOriginPatterns(List.of(ALLOWED_ORIGINS));
            config.setAllowedOrigins(Collections.singletonList("*"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setAllowCredentials(true);
            config.setExposedHeaders(Collections.singletonList("Authorization"));
            config.setMaxAge(3600L);
            return null;
          }
        }
    ));

    http.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
        .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeHttpRequests(request -> request
        .requestMatchers("/temp/**").hasRole("ADMIN")
        .requestMatchers("/api/v1/auth/**").permitAll()
        .anyRequest().authenticated()
    );

    http.formLogin(AbstractHttpConfigurer::disable);
    http.httpBasic(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }


  /**
   * password 강력하게 통제한다면 사용
   */
//  @Bean
//  public CompromisedPasswordChecker compromisedPasswordChecker() {
//    return new HaveIBeenPwnedRestApiPasswordChecker();
//  }
}
