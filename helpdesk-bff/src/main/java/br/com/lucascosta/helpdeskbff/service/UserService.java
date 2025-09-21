package br.com.lucascosta.helpdeskbff.service;

import br.com.lucascosta.helpdeskbff.client.UserFeignClient;
import lombok.RequiredArgsConstructor;
import models.requests.CreateUserRequest;
import models.requests.UpdateUserRequest;
import models.responses.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserFeignClient userFeignClient;

    public List<UserResponse> findAll() {
        return userFeignClient.findAll().getBody();
    }

    public UserResponse findById(final String id) {
        return userFeignClient.findById(id).getBody();
    }

    public UserResponse updateById(final String id, final UpdateUserRequest updateUserRequest) {
        return userFeignClient.updateById(id, updateUserRequest).getBody();
    }

    public void save(final CreateUserRequest createUserRequest) {
        userFeignClient.save(createUserRequest);
    }
}
