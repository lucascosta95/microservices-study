package br.com.lucascosta.authserviceapi.services;

import br.com.lucascosta.authserviceapi.repositories.UserRepository;
import br.com.lucascosta.authserviceapi.security.dtos.UserDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final var entity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User not found: %s", email)));

        return UserDetailsDTO.builder()
                .id(entity.getId())
                .userName(entity.getEmail())
                .password(entity.getPassword())
                .build();
    }
}
