package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.RoleService;
import com.boilerplate.domain.model.Role;
import com.boilerplate.infrastructure.config.ApplicationConfig;
import com.boilerplate.infrastructure.config.DataSeederConfig;
import com.boilerplate.infrastructure.config.JpaConfig;
import com.boilerplate.infrastructure.config.SecurityConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.infrastructure.config.JwtAuthenticationFilter;

@WebMvcTest(controllers = RoleController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        DataSeederConfig.class, ApplicationConfig.class, JpaConfig.class, SecurityConfiguration.class,
        JwtAuthenticationFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllRoles_ShouldReturnList() throws Exception {
        Role role = Role.builder().id(1L).name("USER").build();
        when(roleService.getAllRoles()).thenReturn(Collections.singletonList(role));

        mockMvc.perform(get("/api/v1/roles")
                .with(csrf()))
                .andDo(org.springframework.test.web.servlet.result.MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("USER"));
    }

    @Test
    @WithMockUser
    void createRole_ShouldReturnCreated() throws Exception {
        Role role = Role.builder().name("ADMIN").build();
        when(roleService.createRole(any(Role.class))).thenReturn(role);

        mockMvc.perform(post("/api/v1/roles")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(role)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    @WithMockUser
    void deleteRole_ShouldReturnNoContent() throws Exception {
        doNothing().when(roleService).deleteRole(1L);

        mockMvc.perform(delete("/api/v1/roles/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
