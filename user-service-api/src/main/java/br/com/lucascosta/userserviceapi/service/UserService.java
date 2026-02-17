package br.com.lucascosta.userserviceapi.service;

import br.com.lucascosta.userserviceapi.entity.User;
import br.com.lucascosta.userserviceapi.mapper.UserMapper;
import br.com.lucascosta.userserviceapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import models.exceptions.ResourceNotFoundException;
import models.requests.CreateUserRequest;
import models.requests.UpdateUserRequest;
import models.responses.UserResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper mapper;
    private final UserRepository repository;
    private final BCryptPasswordEncoder encoder;

    @Cacheable(value = "users")
    public List<UserResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromEntity)
                .toList();
    }

    @Cacheable(value = "users", key = "#id")
    public UserResponse findById(final String id) {
        return mapper.fromEntity(find(id));
    }

    @CacheEvict(value = "users", allEntries = true)
    public UserResponse updateById(final String id, final UpdateUserRequest updateUserRequest) {
        var user = find(id);
        verifyIfEmailAlreadyExists(updateUserRequest.email(), id);
        return mapper.fromEntity(
                repository.save(
                        mapper.update(updateUserRequest, user)
                                .withPassword(updateUserRequest.password() != null ? encoder.encode(updateUserRequest.password()) : user.getPassword())
                )
        );
    }

    @CacheEvict(value = "users", allEntries = true)
    public void save(final CreateUserRequest createUserRequest) {
        verifyIfEmailAlreadyExists(createUserRequest.email(), null);
        repository.save(mapper.fromRequest(createUserRequest).withPassword(encoder.encode(createUserRequest.password())));
    }

    private User find(final String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Object not found. id: %s, type: %s", id, UserResponse.class.getSimpleName())
                ));
    }

    private void verifyIfEmailAlreadyExists(final String email, final String id) {
        repository.findByEmail(email)
                .filter(user -> !user.getId().equals(id))
                .ifPresent(user -> {
                    throw new DataIntegrityViolationException(String.format("Email [ %s ]  already exists", email));
                });
    }
}
