package br.com.lucascosta.helpdeskbff.client;

import jakarta.validation.Valid;
import models.requests.CreateUserRequest;
import models.requests.UpdateUserRequest;
import models.responses.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", path = "/api/users")
public interface UserFeignClient {

    @GetMapping()
    ResponseEntity<List<UserResponse>> findAll();

    @GetMapping("/{id}")
    ResponseEntity<UserResponse> findById(@PathVariable("id") final String id);

    @PutMapping("/{id}")
    ResponseEntity<UserResponse> updateById(
            @PathVariable("id") final String id,
            @Valid @RequestBody final UpdateUserRequest updateUserRequest
    );

    @PostMapping()
    ResponseEntity<Void> save(@Valid @RequestBody final CreateUserRequest createUserRequest);
}
