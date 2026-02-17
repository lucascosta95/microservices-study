package br.com.lucascosta.helpdeskbff.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.exceptions.StandardError;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Arrays;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static java.time.LocalDateTime.now;
import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final JWTUtils jwtUtils;
    private final String[] publicRoutes;
    private static final String BEARER = "Bearer ";
    private static final String AUTHORIZATION_NOT_FOUND = "Authorization header is missing";


    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTUtils jwtUtil, String[] publicRoutes) {
        super(authenticationManager);
        this.jwtUtils = jwtUtil;
        this.publicRoutes = publicRoutes;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (isPublicRoute(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        var header = request.getHeader(AUTHORIZATION);
        if (ObjectUtils.isEmpty(header) || !header.startsWith(BEARER)) {
            handleException(request.getRequestURI(), AUTHORIZATION_NOT_FOUND, response);
            return;
        }

        try {
            var auth = getAuthentication(request);
            if (ObjectUtils.isNotEmpty(auth)) {
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (Exception e) {
            handleException(request.getRequestURI(), e.getMessage(), response);
            return;
        }

        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        final var token = request.getHeader(AUTHORIZATION).substring(7);

        var username = jwtUtils.getUsername(token);
        var claims = jwtUtils.getClaims(token);
        var authorities = jwtUtils.getAuthorities(claims);

        return ObjectUtils.isNotEmpty(username)
                ? new UsernamePasswordAuthenticationToken(username, null, authorities)
                : null;
    }

    private boolean isPublicRoute(String uri) {
        return Arrays.stream(this.publicRoutes).anyMatch(uri::startsWith);
    }

    private void handleException(String requestURI, String message, HttpServletResponse response) throws IOException {
        var error = StandardError.builder()
                .timestamp(now())
                .status(UNAUTHORIZED.value())
                .error(UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .path(requestURI)
                .build();

        var mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(WRITE_DATES_AS_TIMESTAMPS);
        var json = mapper.writeValueAsString(error);

        response.setStatus(UNAUTHORIZED.value());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.getWriter().write(json);
    }
}
