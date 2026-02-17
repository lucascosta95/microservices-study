package br.com.lucascosta.helpdeskbff.client;

import br.com.lucascosta.helpdeskbff.config.FeignConfig;
import models.requests.AuthenticateRequest;
import models.requests.RefreshTokenRequest;
import models.responses.AuthenticationResponse;
import models.responses.RefreshTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service-api", path = "/api/auth", configuration = FeignConfig.class)
public interface AuthFeignClient {

    @PostMapping("/login")
    ResponseEntity<AuthenticationResponse> authenticate(@RequestBody final AuthenticateRequest request) throws Exception;

    @PostMapping("/refresh-token")
    ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody final RefreshTokenRequest request);
}
