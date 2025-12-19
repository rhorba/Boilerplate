package com.boilerplate.infrastructure.config;

import com.boilerplate.domain.model.Role;
import com.boilerplate.domain.model.User;
import com.boilerplate.domain.model.UserGroup;
import com.boilerplate.domain.port.out.RoleRepository;
import com.boilerplate.domain.port.out.UserGroupRepository;
import com.boilerplate.domain.port.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeederConfig implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserGroupRepository userGroupRepository;
    private final com.boilerplate.domain.port.out.PageRepository pageRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Seed Roles (Already handled likely by Flyway, but ensuring retrieval is safe)
        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Error: Role ADMIN is not found."));
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Error: Role USER is not found."));

        // Seed Groups
        UserGroup itGroup = createGroupIfNotFound("IT", "Information Technology Department");
        UserGroup hrGroup = createGroupIfNotFound("HR", "Human Resources Department");
        UserGroup salesGroup = createGroupIfNotFound("Sales", "Sales Department");

        // Seed Complex Pages
        com.boilerplate.domain.model.Page dashboardPage = createPageIfNotFound(
                "Dashboard", "dashboard", "home",
                "<h1>Welcome to the Dashboard</h1><p>General overview of system stats.</p>",
                "USER,ADMIN",
                "[{\"name\": \"widget_count\", \"label\": \"Widget Count\", \"type\": \"number\"}]",
                "{\"VIEW\": [\"USER\", \"ADMIN\"], \"EDIT\": [\"ADMIN\"]}");

        com.boilerplate.domain.model.Page userReportPage = createPageIfNotFound(
                "User Report", "user-report", "people",
                "<h1>User Activity Report</h1><p>Confidential HR data.</p>",
                "ADMIN,HR",
                "[{\"name\": \"employee_id\", \"label\": \"Employee ID\", \"type\": \"string\"}, {\"name\": \"status\", \"label\": \"Make Active\", \"type\": \"boolean\", \"uiType\": \"checkbox\"}]",
                "{\"VIEW\": [\"HR\", \"ADMIN\"], \"EDIT\": [\"HR\"]}");

        com.boilerplate.domain.model.Page salesTrackerPage = createPageIfNotFound(
                "Sales Tracker", "sales-tracker", "trending_up",
                "<h1>Q4 Sales Targets</h1><p>Track leads and deals.</p>",
                "SALES,ADMIN",
                "[{\"name\": \"lead_name\", \"label\": \"Lead Name\", \"type\": \"string\"}, {\"name\": \"deal_value\", \"label\": \"Deal Value\", \"type\": \"number\"}, {\"name\": \"stage\", \"label\": \"Stage\", \"type\": \"list\", \"options\": \"New,Negotiation,Closed\"}]",
                "{\"VIEW\": [\"SALES\", \"ADMIN\"], \"EDIT\": [\"SALES\"]}");

        com.boilerplate.domain.model.Page systemConfigPage = createPageIfNotFound(
                "System Config", "system-config", "settings",
                "<h1>System Configuration</h1><p>Advanced settings.</p>",
                "ADMIN,IT",
                "[{\"name\": \"config_key\", \"label\": \"Config Key\", \"type\": \"string\"}, {\"name\": \"enabled\", \"label\": \"Enabled\", \"type\": \"boolean\", \"uiType\": \"toggle\"}]",
                "{\"VIEW\": [\"IT\", \"ADMIN\"], \"EDIT\": [\"IT\", \"ADMIN\"]}");

        com.boilerplate.domain.model.Page hrOnboardingPage = createPageIfNotFound(
                "HR Onboarding", "hr-onboarding", "badge",
                "<h1>New Employee Onboarding</h1><p>Complete the checklist.</p>",
                "HR,ADMIN",
                "[{\"name\": \"personal_email\", \"label\": \"Personal Email\", \"type\": \"email\"}, {\"name\": \"start_date\", \"label\": \"Start Date\", \"type\": \"date\"}, {\"name\": \"documents_signed\", \"label\": \"Documents Signed\", \"type\": \"boolean\", \"uiType\": \"checkbox\"}]",
                "{\"VIEW\": [\"HR\", \"ADMIN\"], \"EDIT\": [\"HR\"]}");

        com.boilerplate.domain.model.Page productFeedbackPage = createPageIfNotFound(
                "Product Feedback", "product-feedback", "feedback",
                "<h1>Customer Feedback Log</h1><p>Record user suggestions.</p>",
                "SALES,Product",
                "[{\"name\": \"rating\", \"label\": \"Rating (1-10)\", \"type\": \"number\"}, {\"name\": \"comments\", \"label\": \"Feedback Details\", \"type\": \"textarea\"}, {\"name\": \"category\", \"label\": \"Category\", \"type\": \"list\", \"options\": \"Bug,Feature,UI/UX\"}]",
                "{\"VIEW\": [\"SALES\", \"ADMIN\"], \"EDIT\": [\"SALES\"]}");

        com.boilerplate.domain.model.Page eventRegPage = createPageIfNotFound(
                "Event Registration", "event-reg", "event",
                "<h1>Company Retreat Registration</h1><p>Sign up now.</p>",
                "USER",
                "[{\"name\": \"dietary_restrictions\", \"label\": \"Dietary Restrictions\", \"type\": \"textarea\"}, {\"name\": \"attending\", \"label\": \"Attending?\", \"type\": \"boolean\", \"uiType\": \"radio\"}]",
                "{\"VIEW\": [\"USER\"], \"EDIT\": [\"USER\"]}");

        // Update Groups with Pages (UserGroup owns the relationship)
        updateGroupWithPages(itGroup, List.of(dashboardPage, systemConfigPage));
        updateGroupWithPages(hrGroup, List.of(dashboardPage, userReportPage, hrOnboardingPage, eventRegPage));
        updateGroupWithPages(salesGroup, List.of(dashboardPage, salesTrackerPage, productFeedbackPage));

        // Seed Users
        createUserIfNotFound("admin@example.com", "admin123", "Admin", "User", adminRole, List.of(itGroup, hrGroup));
        createUserIfNotFound("user@example.com", "user123", "Normal", "User", userRole, List.of());
        createUserIfNotFound("it_user@example.com", "user123", "IT", "Specialist", userRole, List.of(itGroup));
        createUserIfNotFound("hr_manager@example.com", "user123", "HR", "Manager", userRole, List.of(hrGroup));
        createUserIfNotFound("sales_rep@example.com", "user123", "Sales", "Representative", userRole,
                List.of(salesGroup));
    }

    private UserGroup createGroupIfNotFound(String name, String description) {
        return userGroupRepository.findByName(name).orElseGet(() -> {
            UserGroup group = UserGroup.builder()
                    .name(name)
                    .description(description)
                    .build();
            UserGroup saved = userGroupRepository.save(group);
            System.out.println("Created group: " + name);
            return saved;
        });
    }

    private void updateGroupWithPages(UserGroup group, List<com.boilerplate.domain.model.Page> pages) {
        // simple update, in real app might merge
        group.setPages(pages);
        userGroupRepository.save(group);
        System.out.println("Updated group " + group.getName() + " with " + pages.size() + " pages.");
    }

    private com.boilerplate.domain.model.Page createPageIfNotFound(String title, String slug, String icon,
            String content, String roles, String schema, String accessControl) {
        return pageRepository.findBySlug(slug).orElseGet(() -> {
            com.boilerplate.domain.model.Page page = com.boilerplate.domain.model.Page.builder()
                    .title(title)
                    .slug(slug)
                    .icon(icon)
                    .content(content)
                    .roles(roles)
                    .schema(schema)
                    .accessControl(accessControl)
                    .build();
            com.boilerplate.domain.model.Page saved = pageRepository.save(page);
            System.out.println("Created page: " + title);
            return saved;
        });
    }

    private void createUserIfNotFound(String email, String password, String firstname, String lastname, Role role,
            List<UserGroup> groups) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = User.builder()
                    .firstname(firstname)
                    .lastname(lastname)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .role(role)
                    .groups(groups != null ? groups : new ArrayList<>())
                    .build();

            userRepository.save(user);
            System.out.println("Created user: " + email);
        }
    }
}
