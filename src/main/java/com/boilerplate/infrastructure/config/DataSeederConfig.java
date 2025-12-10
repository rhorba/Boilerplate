package com.boilerplate.infrastructure.config;

import com.boilerplate.domain.model.Role;
import com.boilerplate.domain.model.User;
import com.boilerplate.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeederConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.boilerplate.domain.port.out.RoleRepository roleRepository;

    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            // Seed Roles
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ADMIN").build()));
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("USER").build()));

            String adminEmail = "admin@boilerplate.com";
            userRepository.findByEmail(adminEmail).ifPresentOrElse(
                    user -> {
                        // Update role if null (migration fix)
                        if (user.getRole() == null) {
                            User updatedAdmin = User.builder()
                                    .id(user.getId())
                                    .firstname(user.getFirstname())
                                    .lastname(user.getLastname())
                                    .email(user.getEmail())
                                    .password(user.getPassword())
                                    .role(adminRole)
                                    .build();
                            userRepository.save(updatedAdmin);
                            System.out.println("Admin user role updated.");
                        }
                    },
                    () -> {
                        User admin = User.builder()
                                .firstname("Admin")
                                .lastname("User")
                                .email(adminEmail)
                                .password(passwordEncoder.encode("admin123"))
                                .role(adminRole)
                                .build();
                        userRepository.save(admin);
                        System.out.println("Admin user seeded: " + adminEmail);
                    });
        };
    }
}
