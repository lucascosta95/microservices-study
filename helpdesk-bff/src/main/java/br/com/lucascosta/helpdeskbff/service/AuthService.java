package br.com.lucascosta.helpdeskbff.service;

import br.com.lucascosta.helpdeskbff.client.AuthFeignClient;
import lombok.RequiredArgsConstructor;
import models.requests.AuthenticateRequest;
import models.requests.RefreshTokenRequest;
import models.responses.AuthenticationResponse;
import models.responses.RefreshTokenResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthFeignClient authFeignClient;

    public AuthenticationResponse authenticate(AuthenticateRequest request) throws Exception {
        return authFeignClient.authenticate(request).getBody();
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        return authFeignClient.refreshToken(request).getBody();
    }
}
