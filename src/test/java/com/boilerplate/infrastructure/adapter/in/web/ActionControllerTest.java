package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.ActionService;
import com.boilerplate.domain.model.Action;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.boilerplate.infrastructure.config.ApplicationConfig;
import com.boilerplate.infrastructure.config.DataSeederConfig;
import com.boilerplate.infrastructure.config.JpaConfig;
import com.boilerplate.infrastructure.config.SecurityConfiguration;
import com.boilerplate.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(controllers = ActionController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        DataSeederConfig.class, ApplicationConfig.class, JpaConfig.class, SecurityConfiguration.class,
        JwtAuthenticationFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class ActionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActionService actionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllActions_ShouldReturnList() throws Exception {
        List<Action> actions = Arrays.asList(
                Action.builder().id(1L).name("CREATE").build(),
                Action.builder().id(2L).name("DELETE").build());
        when(actionService.getAllActions()).thenReturn(actions);

        mockMvc.perform(get("/api/v1/actions")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createAction_ShouldReturnCreatedAction() throws Exception {
        Action action = Action.builder().name("UPDATE").build();
        when(actionService.createAction(any(Action.class))).thenReturn(action);

        mockMvc.perform(post("/api/v1/actions")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(action)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UPDATE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteAction_ShouldReturnNoContent() throws Exception {
        doNothing().when(actionService).deleteAction(1L);

        mockMvc.perform(delete("/api/v1/actions/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(actionService, times(1)).deleteAction(1L);
    }
}
