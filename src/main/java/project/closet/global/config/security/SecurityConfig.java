package project.closet.global.config.security;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import project.closet.global.config.redis.RedisRepository;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

  private final JWTConfigProperties jwtProperties;
  private final JwtUtils jwtUtils;
  private final RedisRepository redisRepository;

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

  // 임시
  @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {

    // http.cors(corsConfig -> corsConfig.disable());  // cors 불확인 (임시)
    // http.csrf(AbstractHttpConfigurer::disable); // 프론트에서 csrf를 못 찾음
    http.cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
          @Override
          public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOriginPatterns(List.of(ALLOWED_ORIGINS));
            config.setAllowedOrigins(Collections.singletonList("*"));
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setExposedHeaders(Collections.singletonList("Authorization"));
            config.setAllowCredentials(true);
            config.setMaxAge(3600L);
            return config;
          }
        }
    ));

    http.securityContext(contextConfig -> contextConfig.requireExplicitSave(false))
        .sessionManagement(smc -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    http.authorizeHttpRequests(request -> request
        .requestMatchers("/api/auth/sign-in").permitAll() // 로그인 페이지는 permitAll()
        .requestMatchers("/temp/**").hasRole("TEMP") // 임시비밀번호 로그인 시 URL
        .requestMatchers("/api/**").hasRole("USER")
        .requestMatchers("/api/users").hasRole("ADMIN") // TODO : 관리자 전용 URL 나중에 찾기
    );

    http.addFilterBefore(new JWTTokenValidatorFilter(jwtProperties, jwtUtils, redisRepository), UsernamePasswordAuthenticationFilter.class);

    http.formLogin(AbstractHttpConfigurer::disable);
    http.httpBasic(AbstractHttpConfigurer::disable);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider userDetailsProvider(
      UserDetailsService userDetailsService,
      PasswordEncoder passwordEncoder) {

    return new ClosetUserDetailsProvider(userDetailsService, passwordEncoder);
  }

  @Bean
  public GrantedAuthority grantedAuthority() {
    return new SimpleGrantedAuthority("ROLE_");
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationProvider provider) {

    ProviderManager providerManager = new ProviderManager(provider);
    providerManager.setEraseCredentialsAfterAuthentication(true);
    return providerManager;
  }

  /**
   * password 강력하게 통제한다면 사용
   */
//  @Bean
//  public CompromisedPasswordChecker compromisedPasswordChecker() {
//    return new HaveIBeenPwnedRestApiPasswordChecker();
//  }
}
