package com.boilerplate.presentation.controller;

import com.boilerplate.application.dto.request.LoginRequest;
import com.boilerplate.application.dto.request.RegisterRequest;
import com.boilerplate.application.dto.response.AuthResponse;
import com.boilerplate.application.dto.response.UserResponse;
import com.boilerplate.application.service.AuthService;
import com.boilerplate.infrastructure.security.AbacPolicyEvaluator;
import com.boilerplate.infrastructure.security.JwtService;
import com.boilerplate.presentation.exception.DuplicateResourceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @TestConfiguration
    static class TestCorsConfig {
        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(List.of("*"));
            config.setAllowedMethods(List.of("*"));
            config.setAllowedHeaders(List.of("*"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", config);
            return source;
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AbacPolicyEvaluator abacEvaluator;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private AuthResponse mockAuthResponse;

    @BeforeEach
    void setUp() {
        UserResponse userResponse = UserResponse.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .enabled(true)
            .build();

        mockAuthResponse = AuthResponse.builder()
            .accessToken("access-token")
            .refreshToken("refresh-token")
            .tokenType("Bearer")
            .expiresIn(900L)
            .user(userResponse)
            .build();
    }

    @Test
    @WithMockUser
    void login_ValidCredentials_Returns200WithToken() throws Exception {
        LoginRequest request = LoginRequest.builder()
            .username("testuser")
            .password("password123")
            .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(mockAuthResponse);

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"))
            .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void login_MissingUsername_Returns400() throws Exception {
        LoginRequest request = LoginRequest.builder()
            .password("password123")
            .build();

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void login_MissingPassword_Returns400() throws Exception {
        LoginRequest request = LoginRequest.builder()
            .username("testuser")
            .build();

        mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void register_ValidData_Returns201WithToken() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("password123")
            .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(mockAuthResponse);

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").value("access-token"))
            .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @WithMockUser
    void register_MissingEmail_Returns400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
            .username("newuser")
            .password("password123")
            .build();

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void register_InvalidEmail_Returns400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
            .username("newuser")
            .email("not-an-email")
            .password("password123")
            .build();

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void register_ShortPassword_Returns400() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("short")
            .build();

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void register_DuplicateUsername_Returns409() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
            .username("existing")
            .email("new@example.com")
            .password("password123")
            .build();

        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new DuplicateResourceException("Username already exists: existing"));

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser
    void refresh_ValidToken_Returns200() throws Exception {
        when(authService.refreshToken(any(String.class))).thenReturn(mockAuthResponse);

        mockMvc.perform(post("/api/auth/refresh")
                .with(csrf())
                .header("Authorization", "Bearer refresh-token"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").value("access-token"));
    }
}
