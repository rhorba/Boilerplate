package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.ActivityLogService;
import com.boilerplate.domain.model.ActivityLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

@WebMvcTest(controllers = ActivityLogController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        DataSeederConfig.class, ApplicationConfig.class, JpaConfig.class, SecurityConfiguration.class,
        JwtAuthenticationFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class ActivityLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ActivityLogService activityLogService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllLogs_ShouldReturnList() throws Exception {
        ActivityLog log = ActivityLog.builder().id(1L).action("TEST").build();
        when(activityLogService.getAllLogs()).thenReturn(Collections.singletonList(log));

        mockMvc.perform(get("/api/v1/activity-logs")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].action").value("TEST"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void clearLogs_ShouldReturnNoContent() throws Exception {
        doNothing().when(activityLogService).clearLogs();

        mockMvc.perform(delete("/api/v1/activity-logs")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(activityLogService, times(1)).clearLogs();
    }
}
