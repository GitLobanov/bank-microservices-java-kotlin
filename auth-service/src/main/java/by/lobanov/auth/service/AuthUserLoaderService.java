package by.lobanov.auth.service;

import by.lobanov.auth.model.User;
import by.lobanov.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthUserLoaderService implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("bob").isPresent()) {
            User user1 = User.builder()
                    .id(UUID.randomUUID())
                    .password(passwordEncoder.encode("123"))
                    .username("bob")
                    .build();
            userRepository.save(user1);
        }

        if (userRepository.findByUsername("many").isPresent()) {
            User user2 = User.builder()
                    .id(UUID.randomUUID())
                    .password(passwordEncoder.encode("123"))
                    .username("many")
                    .build();
            userRepository.save(user2);
        }
    }
}
