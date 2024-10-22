package br.com.lucascosta.userserviceapi.service;


import br.com.lucascosta.userserviceapi.entity.User;
import br.com.lucascosta.userserviceapi.mapper.UserMapper;
import br.com.lucascosta.userserviceapi.repository.UserRepository;
import models.exceptions.ResourceNotFoundException;
import models.requests.CreateUserRequest;
import models.requests.UpdateUserRequest;
import models.responses.UserResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static br.com.lucascosta.userserviceapi.creator.CreatorUtils.generateMock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    class GetUserById {
        @Test
        void whenCallFindByIdWithValidIdThenReturnUserResponse() {
            when(userRepository.findById(anyString())).thenReturn(Optional.of(new User()));
            when(userMapper.fromEntity(any(User.class))).thenReturn(generateMock(UserResponse.class));

            final var response = userService.findById("1");

            assertNotNull(response);
            assertEquals(UserResponse.class, response.getClass());

            verify(userRepository).findById(anyString());
            verify(userMapper).fromEntity(any(User.class));
        }

        @Test
        void whenCallFindByIdWithInvalidIdThenThrowResourceNotFoundException() {
            when(userRepository.findById(anyString())).thenReturn(Optional.empty());

            try {
                userService.findById("1");
            } catch (Exception e) {
                assertEquals(ResourceNotFoundException.class, e.getClass());
                assertEquals("Object not found. id: 1, type: UserResponse", e.getMessage());
            }

            verify(userRepository).findById(anyString());
            verify(userMapper, times(0)).fromEntity(any(User.class));
        }
    }

    @Nested
    class GetAllUsers {
        @Test
        void whenCallFindAllThenReturnListOfUserResponse() {
            when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));
            when(userMapper.fromEntity(any(User.class))).thenReturn(mock(UserResponse.class));

            final var response = userService.findAll();

            assertNotNull(response);
            assertEquals(2, response.size());
            assertEquals(UserResponse.class, response.get(0).getClass());

            verify(userRepository).findAll();
            verify(userMapper).fromEntity(any(User.class));
        }
    }

    @Nested
    class SaveUser {
        @Test
        void whenCallSaveUserThenSuccess() {
            final var request = generateMock(CreateUserRequest.class);
            when(userMapper.fromRequest(any())).thenReturn(new User());
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(userRepository.save(any(User.class))).thenReturn(new User());
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            userService.save(request);

            verify(userMapper).fromRequest(request);
            verify(passwordEncoder).encode(request.password());
            verify(userRepository).save(any(User.class));
            verify(userRepository).findByEmail(request.email());
        }

        @Test
        void whenCallSaveUserWithInvalidEmailThenThrowDataIntegrityViolationException() {
            final var request = generateMock(CreateUserRequest.class);
            when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(generateMock(User.class)));

            try {
                userService.save(request);
            } catch (Exception e) {
                assertEquals(DataIntegrityViolationException.class, e.getClass());
                assertEquals(String.format("Email [ %s ]  already exists", request.email()), e.getMessage());
            }

            verify(userRepository).findByEmail(request.email());
            verify(userMapper, times(0)).fromRequest(request);
            verify(passwordEncoder, times(0)).encode(request.password());
            verify(userRepository, times(0)).save(any(User.class));
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void whenCallUpdateUserThenSuccess() {
            final var id = "1";
            final var request = generateMock(UpdateUserRequest.class);
            final var entity = generateMock(User.class).withId(id);

            when(userMapper.update(request, entity)).thenReturn(entity);
            when(userRepository.save(any(User.class))).thenReturn(entity);
            when(passwordEncoder.encode(anyString())).thenReturn("encoded");
            when(userRepository.findById(anyString())).thenReturn(Optional.of(entity));
            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(entity));

            userService.updateById(anyString(), request);

            verify(userMapper).fromEntity(entity);
            verify(userMapper).update(request, entity);
            verify(userRepository).findById(anyString());
            verify(userRepository).save(any(User.class));
            verify(userRepository).findByEmail(request.email());
            verify(passwordEncoder).encode(request.password());
        }

        @Test
        void whenCallUpdateUserWithInvalidThenThrowResourceNotFoundException() {
            final var request = generateMock(UpdateUserRequest.class);
            when(userRepository.findById(anyString())).thenReturn(Optional.empty());

            try {
                userService.updateById("1", request);
            } catch (Exception e) {
                assertEquals(ResourceNotFoundException.class, e.getClass());
                assertEquals("Object not found. id: 1, type: UserResponse", e.getMessage());
            }

            verify(userRepository).findByEmail(request.email());
            verify(userMapper, times(0)).update(any(), any());
            verify(userRepository, times(0)).save(any(User.class));
            verify(passwordEncoder, times(0)).encode(request.password());
        }

        @Test
        void whenCallUpdateUserWithInvalidEmailThenThrowDataIntegrityViolationException() {
            final var request = generateMock(UpdateUserRequest.class);
            final var entity = generateMock(User.class);

            when(userRepository.findById(anyString())).thenReturn(Optional.of(entity));
            when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(entity));

            try {
                userService.updateById("1", request);
            } catch (Exception e) {
                assertEquals(ResourceNotFoundException.class, e.getClass());
                assertEquals(String.format("Email [ %s ]  already exists", request.email()), e.getMessage());
            }

            verify(userRepository).findById(anyString());
            verify(userRepository).findByEmail(request.email());
            verify(userMapper, times(0)).update(any(), any());
            verify(userRepository, times(0)).save(any(User.class));
            verify(passwordEncoder, times(0)).encode(request.password());
        }
    }
}