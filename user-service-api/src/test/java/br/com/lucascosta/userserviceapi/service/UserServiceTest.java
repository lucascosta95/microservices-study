package br.com.lucascosta.userserviceapi.service;


import br.com.lucascosta.userserviceapi.entity.User;
import br.com.lucascosta.userserviceapi.mapper.UserMapper;
import br.com.lucascosta.userserviceapi.repository.UserRepository;
import models.exceptions.ResourceNotFoundException;
import models.responses.UserResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

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
            when(userMapper.fromEntity(any(User.class))).thenReturn(mock(UserResponse.class));

            final var response = userService.findById("1");

            assertNotNull(response);
            assertEquals(UserResponse.class, response.getClass());

            verify(userRepository, times(1)).findById(anyString());
            verify(userMapper, times(1)).fromEntity(any(User.class));
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

            verify(userRepository, times(1)).findById(anyString());
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

            verify(userRepository, times(1)).findAll();
            verify(userMapper, times(1)).fromEntity(any(User.class));
        }
    }


}