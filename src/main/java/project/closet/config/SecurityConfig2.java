package project.closet.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyAuthoritiesMapper;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import project.closet.security.CustomSessionInformationExpiredStrategy;
import project.closet.security.JsonUsernamePasswordAuthenticationFilter;
import project.closet.security.SecurityMatchers;
import project.closet.user.entity.Role;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig2 {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            ObjectMapper objectMapper,
            DaoAuthenticationProvider daoAuthenticationProvider,
            SessionRegistry sessionRegistry
    ) throws Exception {
        http
                .authenticationProvider(daoAuthenticationProvider)
                // filter 검사할 URL
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(SecurityMatchers.PUBLIC_MATCHERS).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf ->
                        csrf
                                .ignoringRequestMatchers(SecurityMatchers.LOGOUT)
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                                .sessionAuthenticationStrategy(
                                        new NullAuthenticatedSessionStrategy())
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(SecurityMatchers.LOGOUT)
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler())
                )
                .with(new JsonUsernamePasswordAuthenticationFilter.Configurer(objectMapper),
                        Customizer.withDefaults()
                )
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession()
                        .maximumSessions(-1)
                        .sessionRegistry(sessionRegistry)
                        .expiredSessionStrategy(
                                new CustomSessionInformationExpiredStrategy(objectMapper)
                        )
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

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            RoleHierarchy roleHierarchy
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setAuthoritiesMapper(new RoleHierarchyAuthoritiesMapper(roleHierarchy));
        return provider;
    }

    // Provider 를 이용하여 Manager 생성
    @Bean
    public AuthenticationManager authenticationManager(
            List<AuthenticationProvider> authenticationProviders
    ) {
        return new ProviderManager(authenticationProviders);
    }

    // Role 계층 관계 구조
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.withDefaultRolePrefix()
                .role(Role.ADMIN.name())
                .implies(Role.USER.name())

                .build();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

}
