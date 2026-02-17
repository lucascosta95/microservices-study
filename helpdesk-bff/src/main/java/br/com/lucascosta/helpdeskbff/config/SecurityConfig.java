package br.com.lucascosta.helpdeskbff.config;

import br.com.lucascosta.helpdeskbff.security.JWTAuthorizationFilter;
import br.com.lucascosta.helpdeskbff.security.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtils jwtUtils;
    private final AuthenticationConfiguration authConfig;

    private static final String[] POST_WHITELIST = {"/api/auth/login", "/api/auth/refresh-token"};
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/index.html", "/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/swagger-resources/**", "/webjars/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .addFilterBefore(
                        new JWTAuthorizationFilter(
                                authConfig.getAuthenticationManager(),
                                jwtUtils,
                                ArrayUtils.addAll(POST_WHITELIST, SWAGGER_WHITELIST)
                        ), JWTAuthorizationFilter.class
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                .requestMatchers(POST, POST_WHITELIST).permitAll()
                                .anyRequest().authenticated()
                )
                .build();
    }
}
