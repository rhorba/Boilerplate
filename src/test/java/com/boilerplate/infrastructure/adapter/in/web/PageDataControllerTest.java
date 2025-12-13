package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.PageDataService;
import com.boilerplate.domain.model.PageData;
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
import static org.mockito.Mockito.*;
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

@WebMvcTest(controllers = PageDataController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        DataSeederConfig.class, ApplicationConfig.class, JpaConfig.class, SecurityConfiguration.class,
        JwtAuthenticationFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
class PageDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PageDataService pageDataService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllDataByPageId_ShouldReturnList() throws Exception {
        Long pageId = 1L;
        PageData data = PageData.builder().id(1L).pageId(pageId).data("{}").build();
        when(pageDataService.getAllDataByPageId(pageId)).thenReturn(Collections.singletonList(data));

        mockMvc.perform(get("/api/v1/page-data/page/" + pageId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser
    void getPageDataById_ShouldReturnData() throws Exception {
        Long id = 1L;
        PageData data = PageData.builder().id(id).data("{}").build();
        when(pageDataService.getPageDataById(id)).thenReturn(Optional.of(data));

        mockMvc.perform(get("/api/v1/page-data/" + id)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPageData_ShouldReturnCreated() throws Exception {
        PageData data = PageData.builder().data("{}").build();
        when(pageDataService.createPageData(any(PageData.class))).thenReturn(data);

        mockMvc.perform(post("/api/v1/page-data")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePageData_ShouldReturnUpdated() throws Exception {
        Long id = 1L;
        PageData data = PageData.builder().id(id).data("{\"updated\":true}").build();
        when(pageDataService.updatePageData(eq(id), any(PageData.class))).thenReturn(data);

        mockMvc.perform(put("/api/v1/page-data/" + id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("{\"updated\":true}"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePageData_ShouldReturnNoContent() throws Exception {
        Long id = 1L;
        doNothing().when(pageDataService).deletePageData(id);

        mockMvc.perform(delete("/api/v1/page-data/" + id)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(pageDataService).deletePageData(id);
    }
}
