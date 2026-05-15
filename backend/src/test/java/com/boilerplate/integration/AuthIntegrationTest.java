package com.boilerplate.integration;

import com.boilerplate.application.dto.request.LoginRequest;
import com.boilerplate.application.dto.request.RegisterRequest;
import com.boilerplate.application.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.*;

class AuthIntegrationTest extends AbstractIntegrationTest {

    @Test
    void login_WithSeededAdminCredentials_Returns200WithTokens() {
        LoginRequest request = LoginRequest.builder()
            .username("admin")
            .password("admin123")
            .build();

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
            baseUrl() + "/auth/login",
            request,
            AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
        assertThat(response.getBody().getRefreshToken()).isNotBlank();
        assertThat(response.getBody().getTokenType()).isEqualTo("Bearer");
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("admin");
    }

    @Test
    void login_WithWrongPassword_Returns401() {
        LoginRequest request = LoginRequest.builder()
            .username("admin")
            .password("wrongpassword")
            .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl() + "/auth/login",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_WithNonExistentUser_Returns401() {
        LoginRequest request = LoginRequest.builder()
            .username("doesnotexist")
            .password("password123")
            .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl() + "/auth/login",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void register_WithValidData_Returns201AndAutoLogsIn() {
        RegisterRequest request = RegisterRequest.builder()
            .username("newintegrationuser")
            .email("newintegration@example.com")
            .password("password123")
            .build();

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
            baseUrl() + "/auth/register",
            request,
            AuthResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getAccessToken()).isNotBlank();
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("newintegrationuser");
    }

    @Test
    void register_DuplicateUsername_Returns409() {
        RegisterRequest request = RegisterRequest.builder()
            .username("admin")
            .email("different@example.com")
            .password("password123")
            .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl() + "/auth/register",
            request,
            String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void refreshToken_WithValidRefreshToken_Returns200WithNewAccessToken() {
        // First login to get tokens
        LoginRequest loginRequest = LoginRequest.builder()
            .username("admin")
            .password("admin123")
            .build();

        AuthResponse loginResponse = restTemplate.postForObject(
            baseUrl() + "/auth/login",
            loginRequest,
            AuthResponse.class
        );

        assertThat(loginResponse).isNotNull();
        String refreshToken = loginResponse.getRefreshToken();

        // Use refresh token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + refreshToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<AuthResponse> refreshResponse = restTemplate.exchange(
            baseUrl() + "/auth/refresh",
            HttpMethod.POST,
            entity,
            AuthResponse.class
        );

        assertThat(refreshResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(refreshResponse.getBody()).isNotNull();
        assertThat(refreshResponse.getBody().getAccessToken()).isNotBlank();
    }
}
