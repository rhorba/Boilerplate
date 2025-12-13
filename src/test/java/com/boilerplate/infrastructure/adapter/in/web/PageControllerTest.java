package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.PageService;
import com.boilerplate.domain.model.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.boilerplate.infrastructure.config.ApplicationConfig;
import com.boilerplate.infrastructure.config.DataSeederConfig;
import com.boilerplate.infrastructure.config.JpaConfig;
import com.boilerplate.infrastructure.config.SecurityConfiguration;
import com.boilerplate.infrastructure.config.JwtAuthenticationFilter;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@WebMvcTest(controllers = PageController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
        DataSeederConfig.class, ApplicationConfig.class, JpaConfig.class, SecurityConfiguration.class,
        JwtAuthenticationFilter.class }))
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PageService pageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void getAllPages_ShouldReturnList() throws Exception {
        when(pageService.getAllPages()).thenReturn(List.of(Page.builder().title("Test").build()));

        mockMvc.perform(get("/api/v1/pages")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getPageBySlug_ShouldReturnPage() throws Exception {
        String slug = "test-page";
        when(pageService.getPageBySlug(slug)).thenReturn(Optional.of(Page.builder().slug(slug).build()));

        mockMvc.perform(get("/api/v1/pages/" + slug)
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPage_ShouldReturnPage() throws Exception {
        Page page = Page.builder().title("New Page").build();
        when(pageService.createPage(any(Page.class))).thenReturn(page);

        mockMvc.perform(post("/api/v1/pages")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(page)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePage_ShouldReturnPage() throws Exception {
        Long id = 1L;
        Page page = Page.builder().title("Updated").build();
        when(pageService.updatePage(eq(id), any(Page.class))).thenReturn(page);

        mockMvc.perform(put("/api/v1/pages/" + id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(page)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePage_ShouldReturnNoContent() throws Exception {
        Long id = 1L;
        doNothing().when(pageService).deletePage(id);

        mockMvc.perform(delete("/api/v1/pages/" + id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
