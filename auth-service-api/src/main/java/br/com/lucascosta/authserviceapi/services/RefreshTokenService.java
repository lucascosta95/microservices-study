package br.com.lucascosta.authserviceapi.services;

import br.com.lucascosta.authserviceapi.models.RefreshToken;
import br.com.lucascosta.authserviceapi.repositories.RefreshTokenRepository;
import br.com.lucascosta.authserviceapi.security.dtos.UserDetailsDTO;
import br.com.lucascosta.authserviceapi.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import models.exceptions.RefreshTokenExpired;
import models.exceptions.ResourceNotFoundException;
import models.responses.RefreshTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.expiration-sec.refresh.token}")
    private Long refreshTokenExpirationSec;

    private final JWTUtils jwtUtils;
    private final RefreshTokenRepository repository;
    private final UserDetailsService userDetailsService;

    public RefreshToken save(final String username) {
        return repository.save(
                RefreshToken.builder()
                        .id(UUID.randomUUID().toString())
                        .createdAt(LocalDateTime.now())
                        .expiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpirationSec))
                        .username(username)
                        .build()
        );
    }

    public RefreshTokenResponse refreshToken(final String refreshTokenId) {
        final var refreshToken = repository.findById(refreshTokenId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Refresh token not found. Id: %s", refreshTokenId)));

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenExpired(String.format("Refresh token expired. Id: %s", refreshTokenId));
        }

        return new RefreshTokenResponse(
                jwtUtils.generateToken((UserDetailsDTO) userDetailsService.loadUserByUsername(refreshToken.getUsername()))
        );
    }
}
