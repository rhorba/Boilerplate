package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.UserService;
import com.boilerplate.domain.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.infrastructure.config.ApplicationConfig;
import com.boilerplate.infrastructure.config.DataSeederConfig;
import com.boilerplate.infrastructure.config.JpaConfig;
import com.boilerplate.infrastructure.config.SecurityConfiguration;
import com.boilerplate.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(controllers = UserController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        DataSeederConfig.class, ApplicationConfig.class, JpaConfig.class, SecurityConfiguration.class,
        JwtAuthenticationFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser(roles = "ADMIN")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllUsers_ShouldReturnList() throws Exception {
        User user = User.builder().id(1L).email("test@example.com").build();
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(user));

        mockMvc.perform(get("/api/v1/users")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        Long id = 1L;
        User user = User.builder().id(id).email("test@example.com").build();
        when(userService.getUserById(id)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/api/v1/users/" + id)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void updateUser_ShouldReturnUpdated() throws Exception {
        Long id = 1L;
        User user = User.builder().id(id).firstname("Updated").build();
        when(userService.updateUser(eq(id), any(User.class))).thenReturn(user);

        mockMvc.perform(put("/api/v1/users/" + id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Updated"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/users/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
