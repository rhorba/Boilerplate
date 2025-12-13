package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.dto.AuthenticationRequest;
import com.boilerplate.application.dto.AuthenticationResponse;
import com.boilerplate.application.dto.RegisterRequest;
import com.boilerplate.application.service.AuthService;
import com.boilerplate.domain.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.infrastructure.config.ApplicationConfig;
import com.boilerplate.infrastructure.config.DataSeederConfig;
import com.boilerplate.infrastructure.config.JpaConfig;
import com.boilerplate.infrastructure.config.SecurityConfiguration;
import com.boilerplate.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(controllers = AuthController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                DataSeederConfig.class, ApplicationConfig.class, JpaConfig.class, SecurityConfiguration.class,
                JwtAuthenticationFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService authService;

        @Autowired
        private ObjectMapper objectMapper;

        @Test
        @WithMockUser
        void register_ShouldReturnOk() throws Exception {
                RegisterRequest request = RegisterRequest.builder()
                                .firstname("John")
                                .lastname("Doe")
                                .email("john@example.com")
                                .password("password")
                                .build();
                AuthenticationResponse response = AuthenticationResponse.builder()
                                .accessToken("access")
                                .refreshToken("refresh")
                                .build();

                when(authService.register(any(RegisterRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/v1/auth/register")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void authenticate_ShouldReturnOk() throws Exception {
                AuthenticationRequest request = AuthenticationRequest.builder()
                                .email("john@example.com")
                                .password("password")
                                .build();
                AuthenticationResponse response = AuthenticationResponse.builder()
                                .accessToken("access")
                                .refreshToken("refresh")
                                .build();

                when(authService.authenticate(any(AuthenticationRequest.class))).thenReturn(response);

                mockMvc.perform(post("/api/v1/auth/authenticate")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser
        void getProfile_ShouldReturnOk() throws Exception {
                User user = User.builder().email("test@example.com").build();
                when(authService.getCurrentUser()).thenReturn(user);

                mockMvc.perform(get("/api/v1/auth/me")
                                .with(csrf()))
                                .andExpect(status().isOk());
        }
}
