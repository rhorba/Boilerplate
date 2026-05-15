package com.boilerplate.presentation.controller;

import com.boilerplate.application.dto.request.UpdateProfileRequest;
import com.boilerplate.application.dto.response.UserProfileResponse;
import com.boilerplate.application.service.UserProfileService;
import com.boilerplate.domain.model.User;
import com.boilerplate.infrastructure.security.AbacPolicyEvaluator;
import com.boilerplate.infrastructure.security.JwtService;
import com.boilerplate.infrastructure.security.UserPrincipal;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserProfileController.class)
class UserProfileControllerTest {

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
    private UserProfileService userProfileService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AbacPolicyEvaluator abacEvaluator;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UserPrincipal userPrincipal;
    private UserProfileResponse testProfileResponse;

    @BeforeEach
    void setUp() {
        User user = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .password("encodedPassword")
            .enabled(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .build();

        userPrincipal = new UserPrincipal(user);

        testProfileResponse = UserProfileResponse.builder()
            .firstName("John")
            .lastName("Doe")
            .bio("Test bio")
            .phoneNumber("+1234567890")
            .build();
    }

    @Test
    void getMyProfile_Authenticated_Returns200() throws Exception {
        when(userProfileService.getProfile(1L)).thenReturn(testProfileResponse);

        mockMvc.perform(get("/api/profile/me")
                .with(user(userPrincipal)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("John"))
            .andExpect(jsonPath("$.bio").value("Test bio"));
    }

    @Test
    void getMyProfile_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/profile/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void upsertMyProfile_Authenticated_Returns200() throws Exception {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
            .firstName("Jane")
            .lastName("Smith")
            .bio("Updated bio")
            .phoneNumber("+0987654321")
            .build();

        when(userProfileService.upsertProfile(eq(1L), any(UpdateProfileRequest.class)))
            .thenReturn(testProfileResponse);

        mockMvc.perform(put("/api/profile/me")
                .with(user(userPrincipal))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    @WithMockUser
    void upsertMyProfile_InvalidPhoneNumber_Returns400() throws Exception {
        UpdateProfileRequest request = UpdateProfileRequest.builder()
            .phoneNumber("this-phone-is-way-too-long-exceeding-the-20-char-limit-definitely")
            .build();

        mockMvc.perform(put("/api/profile/me")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }
}
