package com.boilerplate.integration;

import com.boilerplate.application.dto.request.CreateUserRequest;
import com.boilerplate.application.dto.request.LoginRequest;
import com.boilerplate.application.dto.response.AuthResponse;
import com.boilerplate.application.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

class UserIntegrationTest extends AbstractIntegrationTest {

    private String adminToken;

    @BeforeEach
    void authenticate() {
        LoginRequest loginRequest = LoginRequest.builder()
            .username("admin")
            .password("admin123")
            .build();

        AuthResponse authResponse = restTemplate.postForObject(
            baseUrl() + "/auth/login",
            loginRequest,
            AuthResponse.class
        );

        assertThat(authResponse).isNotNull();
        adminToken = authResponse.getAccessToken();
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    @Test
    void getUsers_WithAdminToken_Returns200WithList() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl() + "/users",
            HttpMethod.GET,
            entity,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("admin");
    }

    @Test
    void getUsers_WithoutToken_Returns401() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            baseUrl() + "/users",
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void createUser_WithAdminToken_Returns201() {
        CreateUserRequest request = CreateUserRequest.builder()
            .username("integrationtestuser")
            .email("integrationtest@example.com")
            .password("password123")
            .build();

        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(request, authHeaders());

        ResponseEntity<UserResponse> response = restTemplate.exchange(
            baseUrl() + "/users",
            HttpMethod.POST,
            entity,
            UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("integrationtestuser");
        assertThat(response.getBody().getEmail()).isEqualTo("integrationtest@example.com");
        assertThat(response.getBody().getEnabled()).isTrue();
    }

    @Test
    void createUser_DuplicateUsername_Returns409() {
        CreateUserRequest request = CreateUserRequest.builder()
            .username("admin")
            .email("another@example.com")
            .password("password123")
            .build();

        HttpEntity<CreateUserRequest> entity = new HttpEntity<>(request, authHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl() + "/users",
            HttpMethod.POST,
            entity,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void getUserById_ExistingAdmin_Returns200() {
        // First get all users to find admin ID
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());

        // Get admin user by username
        ResponseEntity<UserResponse> response = restTemplate.exchange(
            baseUrl() + "/users/username/admin",
            HttpMethod.GET,
            entity,
            UserResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("admin");
    }

    @Test
    void getUserById_NotFound_Returns404() {
        HttpEntity<Void> entity = new HttpEntity<>(authHeaders());

        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl() + "/users/99999",
            HttpMethod.GET,
            entity,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void createAndDeleteUser_FullCycle() {
        // Create
        CreateUserRequest createRequest = CreateUserRequest.builder()
            .username("deletemeuser")
            .email("deleteme@example.com")
            .password("password123")
            .build();

        HttpEntity<CreateUserRequest> createEntity = new HttpEntity<>(createRequest, authHeaders());
        ResponseEntity<UserResponse> createResponse = restTemplate.exchange(
            baseUrl() + "/users",
            HttpMethod.POST,
            createEntity,
            UserResponse.class
        );

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long userId = createResponse.getBody().getId();

        // Delete (soft)
        HttpEntity<Void> deleteEntity = new HttpEntity<>(authHeaders());
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
            baseUrl() + "/users/" + userId,
            HttpMethod.DELETE,
            deleteEntity,
            Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // Restore
        ResponseEntity<UserResponse> restoreResponse = restTemplate.exchange(
            baseUrl() + "/users/" + userId + "/restore",
            HttpMethod.POST,
            deleteEntity,
            UserResponse.class
        );

        assertThat(restoreResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(restoreResponse.getBody().getDeletedAt()).isNull();
    }
}
