package com.boilerplate.presentation.controller;

import com.boilerplate.application.dto.request.GroupRequest;
import com.boilerplate.application.dto.response.GroupResponse;
import com.boilerplate.application.service.GroupService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

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
    private GroupService groupService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AbacPolicyEvaluator abacEvaluator;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    private GroupResponse testGroupResponse;

    @BeforeEach
    void setUp() {
        testGroupResponse = new GroupResponse(
            1L,
            "Admins",
            "Administrator group",
            Set.of(),
            0,
            null,
            null
        );

        when(abacEvaluator.hasPermission(any(), anyString(), anyString())).thenReturn(true);
        when(abacEvaluator.hasPermission(any(), anyString(), anyString(), any())).thenReturn(true);
    }

    @Test
    @WithMockUser
    void getAllGroups_WithPermission_Returns200() throws Exception {
        when(groupService.getAllGroups()).thenReturn(List.of(testGroupResponse));

        mockMvc.perform(get("/api/groups"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Admins"))
            .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void getAllGroups_Unauthenticated_Returns401() throws Exception {
        mockMvc.perform(get("/api/groups"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getGroupById_WithPermission_Returns200() throws Exception {
        when(groupService.getGroupById(1L)).thenReturn(testGroupResponse);

        mockMvc.perform(get("/api/groups/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Admins"));
    }

    @Test
    @WithMockUser
    void getGroupById_NotFound_Returns404() throws Exception {
        when(groupService.getGroupById(99L))
            .thenThrow(new ResourceNotFoundException("Group not found with id: 99"));

        mockMvc.perform(get("/api/groups/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void createGroup_ValidRequest_Returns201() throws Exception {
        GroupRequest request = new GroupRequest("New Group", "Description");
        when(groupService.createGroup(any(GroupRequest.class))).thenReturn(testGroupResponse);

        mockMvc.perform(post("/api/groups")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Admins"));
    }

    @Test
    @WithMockUser
    void createGroup_MissingName_Returns400() throws Exception {
        GroupRequest request = new GroupRequest("", "Description");

        mockMvc.perform(post("/api/groups")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser
    void updateGroup_ValidRequest_Returns200() throws Exception {
        GroupRequest request = new GroupRequest("Updated Group", "Updated desc");
        when(groupService.updateGroup(eq(1L), any(GroupRequest.class))).thenReturn(testGroupResponse);

        mockMvc.perform(put("/api/groups/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteGroup_WithPermission_Returns204() throws Exception {
        mockMvc.perform(delete("/api/groups/1")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser
    void removeUserFromGroup_WithPermission_Returns204() throws Exception {
        mockMvc.perform(delete("/api/groups/1/users/2")
                .with(csrf()))
            .andExpect(status().isNoContent());
    }
}
