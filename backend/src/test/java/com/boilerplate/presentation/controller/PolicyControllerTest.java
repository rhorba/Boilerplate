package com.boilerplate.presentation.controller;

import com.boilerplate.application.dto.request.PolicyRequest;
import com.boilerplate.application.dto.response.PolicyResponse;
import com.boilerplate.application.service.PolicyService;
import com.boilerplate.domain.model.Policy.PolicyAction;
import com.boilerplate.domain.model.Policy.PolicyEffect;
import com.boilerplate.domain.model.Policy.PolicyResource;
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
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PolicyController.class)
class PolicyControllerTest {

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
    private PolicyService policyService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AbacPolicyEvaluator abacEvaluator;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private PolicyResponse testPolicyResponse;

    @BeforeEach
    void setUp() {
        testPolicyResponse = new PolicyResponse(
            1L, "test-policy", null,
            PolicyEffect.PERMIT, PolicyResource.USER, PolicyAction.READ,
            true, Set.of(), null, null
        );
        when(abacEvaluator.hasPermission(any(), any(), any())).thenReturn(true);
        when(abacEvaluator.hasPermission(any(), any(), any(), any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    void getAllPolicies_WithAuth_Returns200() throws Exception {
        when(policyService.getAllPolicies()).thenReturn(List.of(testPolicyResponse));

        mockMvc.perform(get("/api/policies"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("test-policy"));
    }

    @Test
    void getAllPolicies_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/policies"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getPolicyById_WithAuth_Returns200() throws Exception {
        when(policyService.getPolicyById(1L)).thenReturn(testPolicyResponse);

        mockMvc.perform(get("/api/policies/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("test-policy"));
    }

    @Test
    @WithMockUser
    void getPolicyById_NotFound_Returns404() throws Exception {
        when(policyService.getPolicyById(99L))
            .thenThrow(new ResourceNotFoundException("Policy not found with id: 99"));

        mockMvc.perform(get("/api/policies/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createPolicy_WithAuth_Returns201() throws Exception {
        PolicyRequest request = new PolicyRequest(
            "new-policy", null, PolicyEffect.PERMIT,
            PolicyResource.USER, PolicyAction.READ, true, null
        );
        when(policyService.createPolicy(any(PolicyRequest.class))).thenReturn(testPolicyResponse);

        mockMvc.perform(post("/api/policies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("test-policy"));
    }

    @Test
    @WithMockUser
    void createPolicy_MissingName_Returns400() throws Exception {
        String requestBody = "{\"effect\":\"PERMIT\",\"resource\":\"USER\",\"action\":\"READ\"}";

        mockMvc.perform(post("/api/policies")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updatePolicy_WithAuth_Returns200() throws Exception {
        PolicyRequest request = new PolicyRequest(
            "updated-policy", null, PolicyEffect.DENY,
            PolicyResource.USER, PolicyAction.DELETE, true, null
        );
        when(policyService.updatePolicy(eq(1L), any(PolicyRequest.class)))
            .thenReturn(testPolicyResponse);

        mockMvc.perform(put("/api/policies/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deletePolicy_WithAuth_Returns204() throws Exception {
        mockMvc.perform(delete("/api/policies/1")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}
