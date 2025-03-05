package br.com.lucascosta.orderserviceapi.clients;

import io.swagger.v3.oas.annotations.Parameter;
import models.responses.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service-api", url = "http://localhost:8080/api/users")
public interface UserServiceFeignClient {

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> findById(
            @Parameter(description = "User id", required = true, example = "66ff3db12ffcaa1cf7d3ced0")
            @PathVariable("id") final String id
    );
}
