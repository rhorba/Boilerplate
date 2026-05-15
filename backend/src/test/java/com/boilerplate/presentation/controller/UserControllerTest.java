package com.boilerplate.presentation.controller;

import com.boilerplate.application.dto.request.CreateUserRequest;
import com.boilerplate.application.dto.request.UpdateUserRequest;
import com.boilerplate.application.dto.response.BulkActionResponse;
import com.boilerplate.application.dto.response.UserResponse;
import com.boilerplate.application.service.UserService;
import com.boilerplate.infrastructure.security.AbacPolicyEvaluator;
import com.boilerplate.infrastructure.security.JwtService;
import com.boilerplate.presentation.exception.ResourceNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

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
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AbacPolicyEvaluator abacEvaluator;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUserResponse = UserResponse.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .enabled(true)
            .build();

        // Default: evaluator permits all in controller tests
        when(abacEvaluator.hasPermission(any(), anyString(), anyString())).thenReturn(true);
        when(abacEvaluator.hasPermission(any(), anyString(), anyString(), any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    void getAllUsers_WithPermission_Returns200() throws Exception {
        Page<UserResponse> page = new PageImpl<>(List.of(testUserResponse));
        when(userService.searchUsers(any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].username").value("testuser"));
    }

    @Test
    void getAllUsers_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getUserById_WithPermission_Returns200() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUserResponse);

        mockMvc.perform(get("/api/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void getUserById_NotFound_Returns404() throws Exception {
        when(userService.getUserById(99L)).thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createUser_WithPermission_Returns201() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
            .username("newuser")
            .email("new@example.com")
            .password("password123")
            .build();

        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(testUserResponse);

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser
    void createUser_MissingUsername_Returns400() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder()
            .email("new@example.com")
            .password("password123")
            .build();

        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateUser_WithPermission_Returns200() throws Exception {
        UpdateUserRequest request = UpdateUserRequest.builder()
            .username("updated")
            .email("updated@example.com")
            .build();

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class))).thenReturn(testUserResponse);

        mockMvc.perform(put("/api/users/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteUser_WithPermission_Returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void restoreUser_WithPermission_Returns200() throws Exception {
        when(userService.restoreUser(1L)).thenReturn(testUserResponse);

        mockMvc.perform(post("/api/users/1/restore")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser
    void purgeUser_WithPermissions_Returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1/purge")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void bulkDelete_WithPermission_Returns200() throws Exception {
        String requestBody = "{\"userIds\": [1, 2, 3]}";
        BulkActionResponse response = BulkActionResponse.builder()
            .affected(3)
            .message("3 users deleted")
            .build();

        when(userService.bulkSoftDelete(any())).thenReturn(3);

        mockMvc.perform(post("/api/users/bulk/delete")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.affected").value(3));
    }
}
