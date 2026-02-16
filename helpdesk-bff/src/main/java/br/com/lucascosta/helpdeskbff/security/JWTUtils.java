package br.com.lucascosta.helpdeskbff.security;

import io.jsonwebtoken.*;
import models.exceptions.JWTCustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String secret;

    private static final String AUTHORITY = "authority";
    private static final String AUTHORITIES = "authorities";

    public Claims getClaims(final String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException ex) {
            throw new JWTCustomException(ex.getMessage());
        }
    }

    public String getUsername(String token) {
        var claims = getClaims(token);
        return claims.getSubject() != null ? claims.getSubject() : null;
    }

    public List<GrantedAuthority> getAuthorities(Claims claims) {
        if (claims.get(AUTHORITIES) == null) {
            throw new JWTCustomException("Invalid token");
        }

        @SuppressWarnings("unchecked")
        var authorities = (List<LinkedHashMap<String, String>>) claims.get(AUTHORITIES);

        return authorities
                .stream()
                .map(authority -> (GrantedAuthority) () -> authority.get(AUTHORITY))
                .toList();
    }
}
