package br.com.lucascosta.helpdeskbff.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

import static org.springframework.cloud.config.client.ConfigClientProperties.AUTHORIZATION;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final JWTUtils jwtUtils;
    private static final String BEARER = "Bearer ";

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, JWTUtils jwtUtils) {
        super(authenticationManager);
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final var header = request.getHeader(AUTHORIZATION);
        if (ObjectUtils.isEmpty(header) || !header.startsWith(BEARER)) {
            chain.doFilter(request, response);
            return;
        }

        var auth = getAuthentication(request);
        if (ObjectUtils.isNotEmpty(auth)) {
            SecurityContextHolder.getContext().setAuthentication(auth);
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
}
