package com.boilerplate.presentation.controller;

import com.boilerplate.application.dto.request.UserAttributeRequest;
import com.boilerplate.application.dto.response.UserAttributeResponse;
import com.boilerplate.application.service.UserAttributeService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserAttributeController.class)
class UserAttributeControllerTest {

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
    private UserAttributeService userAttributeService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AbacPolicyEvaluator abacEvaluator;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private UserAttributeResponse testAttributeResponse;

    @BeforeEach
    void setUp() {
        testAttributeResponse = new UserAttributeResponse(1L, "dept", "engineering");
        when(abacEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
        when(abacEvaluator.hasPermission(any(), any(), any(), any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    void getAttributes_WithAuth_Returns200() throws Exception {
        when(userAttributeService.getAttributesForUser(1L)).thenReturn(List.of(testAttributeResponse));

        mockMvc.perform(get("/api/users/1/attributes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].attributeKey").value("dept"));
    }

    @Test
    void getAttributes_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/users/1/attributes"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAttributes_UserNotFound_Returns404() throws Exception {
        when(userAttributeService.getAttributesForUser(99L))
            .thenThrow(new ResourceNotFoundException("User not found with id: 99"));

        mockMvc.perform(get("/api/users/99/attributes"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void setAttribute_WithAuth_Returns200() throws Exception {
        UserAttributeRequest request = new UserAttributeRequest("dept", "engineering");
        when(userAttributeService.setUserAttribute(eq(1L), any(UserAttributeRequest.class)))
            .thenReturn(testAttributeResponse);

        mockMvc.perform(put("/api/users/1/attributes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.attributeKey").value("dept"));
    }

    @Test
    @WithMockUser
    void setAttribute_MissingKey_Returns400() throws Exception {
        String requestBody = "{\"attributeValue\":\"engineering\"}";

        mockMvc.perform(put("/api/users/1/attributes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void deleteAttribute_WithAuth_Returns204() throws Exception {
        mockMvc.perform(delete("/api/users/1/attributes/dept")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}
