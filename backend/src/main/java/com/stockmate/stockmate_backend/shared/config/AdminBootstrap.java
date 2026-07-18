package com.stockmate.stockmate_backend.shared.config;

import com.stockmate.stockmate_backend.shared.entity.Role;
import com.stockmate.stockmate_backend.shared.entity.User;
import com.stockmate.stockmate_backend.userauthentication.repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class AdminBootstrap {

    private final AdminProperties adminProperties;
    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner createAdmin() {
        return args -> {
            if (!adminProperties.isEnabled()) {
                return;
            }

            if (adminProperties.getEmail() == null || adminProperties.getEmail().isBlank()) {
                throw new IllegalStateException("app.admin.email must be set when admin bootstrap is enabled");
            }

            if (adminProperties.getPassword() == null || adminProperties.getPassword().isBlank()) {
                throw new IllegalStateException("app.admin.password must be set when admin bootstrap is enabled");
            }

            if (userRepository.findByEmail(adminProperties.getEmail()).isPresent()) {
                return;
            }

            User admin = User.builder()
                    .name("Admin")
                    .email(adminProperties.getEmail())
                    .passwordHash(passwordEncoder.encode(adminProperties.getPassword()))
                    .role(Role.ADMIN)
                    .isActive(true)
                    .build();

            userRepository.save(admin);
        };
    }
}
