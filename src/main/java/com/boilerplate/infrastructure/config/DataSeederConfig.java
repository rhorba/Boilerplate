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

    private final com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository springDataUserRepository;
    private final com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataRoleRepository springDataRoleRepository;
    private final com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataActionRepository springDataActionRepository;
    private final com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataPageRepository springDataPageRepository;
    private final com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataActivityLogRepository springDataActivityLogRepository;

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.boilerplate.domain.port.out.RoleRepository roleRepository;
    private final com.boilerplate.domain.port.out.ActionRepository actionRepository;
    private final com.boilerplate.domain.port.out.PageRepository pageRepository;
    private final com.boilerplate.domain.port.out.ActivityLogRepository activityLogRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    @Bean
    public CommandLineRunner seedDatabase() {
        return args -> {
            // 0. CLEAN DATABASE
            System.out.println("Cleaning database...");
            springDataActivityLogRepository.deleteAll();
            springDataPageRepository.deleteAll();
            springDataUserRepository.deleteAll();
            springDataRoleRepository.deleteAll();
            springDataActionRepository.deleteAll();
            System.out.println("Database cleaned.");

            // 1. Seed Roles (6 roles)
            String[] roleNames = { "ADMIN", "USER", "MANAGER", "EDITOR", "VIEWER", "GUEST" };
            for (String roleName : roleNames) {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    roleRepository.save(Role.builder().name(roleName).build());
                }
            }
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            Role userRole = roleRepository.findByName("USER").orElseThrow();

            // 2. Seed Actions (6 actions)
            String[] actionNames = { "create", "read", "update", "delete", "approve", "reject" };
            for (String actionName : actionNames) {
                if (actionRepository.findAll().stream().noneMatch(a -> a.getName().equals(actionName))) {
                    actionRepository.save(com.boilerplate.domain.model.Action.builder().name(actionName).build());
                }
            }

            // 3. Seed Users (6 users with different roles)
            // Admin
            String adminEmail = "admin@boilerplate.com";
            User admin = User.builder()
                    .firstname("Admin")
                    .lastname("User")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .build();
            userRepository.save(admin);

            // 5 Regular Users with varied roles
            String[] userRoles = { "USER", "MANAGER", "EDITOR", "VIEWER", "GUEST" };
            for (int i = 0; i < 5; i++) {
                String email = "user" + (i + 1) + "@boilerplate.com";
                String roleName = userRoles[i];
                Role role = roleRepository.findByName(roleName).orElse(userRole);

                User user = User.builder()
                        .firstname("User")
                        .lastname(String.valueOf(i + 1))
                        .email(email)
                        .password(passwordEncoder.encode("password"))
                        .role(role)
                        .build();
                userRepository.save(user);
            }

            // 4. Seed Pages (6 pages with Access Control)
            String[] pageTitles = { "Dashboard", "Profile", "Settings", "Reports", "Analytics", "Help" };
            for (String title : pageTitles) {
                String slug = title.toLowerCase();

                // Define Access Control based on page
                java.util.Map<String, java.util.List<String>> accessControl = new java.util.HashMap<>();
                if (title.equals("Settings")) {
                    accessControl.put("read", java.util.List.of("ADMIN"));
                    accessControl.put("update", java.util.List.of("ADMIN"));
                } else if (title.equals("Reports")) {
                    accessControl.put("read", java.util.List.of("ADMIN", "MANAGER"));
                    accessControl.put("create", java.util.List.of("MANAGER"));
                } else {
                    // Default: Read for everyone, Update for Admin
                    accessControl.put("read", java.util.List.of("ADMIN", "USER", "MANAGER", "EDITOR", "VIEWER"));
                    accessControl.put("update", java.util.List.of("ADMIN", "EDITOR"));
                }

                String accessControlJson = "{}";
                try {
                    accessControlJson = objectMapper.writeValueAsString(accessControl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                com.boilerplate.domain.model.Page page = com.boilerplate.domain.model.Page.builder()
                        .title(title)
                        .slug(slug)
                        .content("<h1>" + title + "</h1><p>Welcome to " + title + " page.</p>")
                        .icon("article")
                        .roles("USER,ADMIN") // Legacy field
                        .accessControl(accessControlJson)
                        .build();
                pageRepository.save(page);
            }

            // 5. Seed Activity Logs (6 logs)
            for (int i = 1; i <= 6; i++) {
                com.boilerplate.domain.model.ActivityLog log = com.boilerplate.domain.model.ActivityLog.builder()
                        .action("SYSTEM_EVENT")
                        .description("Seeded log entry #" + i)
                        .userEmail("system@boilerplate.com")
                        .timestamp(java.time.LocalDateTime.now().minusHours(i))
                        .build();
                activityLogRepository.save(log);
            }

            // 6. Seed User Groups
            com.boilerplate.domain.port.out.UserGroupRepository userGroupRepository = (com.boilerplate.domain.port.out.UserGroupRepository) org.springframework.context.ApplicationContextAware.class
                    .cast(null); // This is getting messy with dependency injection in args.
            // Better to inject it in the class.
        };
    }
}
