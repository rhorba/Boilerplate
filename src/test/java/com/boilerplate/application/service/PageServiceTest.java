package com.boilerplate.application.service;

import com.boilerplate.domain.model.Page;
import com.boilerplate.domain.model.UserGroup;
import com.boilerplate.domain.port.out.PageRepository;
import com.boilerplate.domain.port.out.UserGroupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PageServiceTest {

    @Mock
    private PageRepository pageRepository;
    @Mock
    private UserGroupRepository userGroupRepository;
    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private PageService pageService;

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;
    private SecurityContext securityContext;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        securityContextHolderMock = mockStatic(SecurityContextHolder.class);
        securityContext = mock(SecurityContext.class);
        authentication = mock(Authentication.class);

        securityContextHolderMock.when(SecurityContextHolder::getContext).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    @AfterEach
    void tearDown() {
        securityContextHolderMock.close();
    }

    @Test
    void createPage_ShouldSaveAndLog() {
        Page page = Page.builder().title("Test Page").build();
        when(pageRepository.save(page)).thenReturn(page);
        when(authentication.getName()).thenReturn("admin@example.com");

        Page result = pageService.createPage(page);

        assertEquals("test-page", result.getSlug());
        verify(pageRepository).save(page);
        verify(activityLogService).log(eq("CREATE_PAGE"), anyString(), eq("admin@example.com"));
    }

    @Test
    void updatePage_ShouldUpdateAndLog() {
        Long id = 1L;
        Page existing = Page.builder().id(id).title("Old Title").build();
        Page updated = Page.builder().id(id).title("New Title").groups(List.of()).build();

        when(pageRepository.findById(id)).thenReturn(Optional.of(existing));
        when(pageRepository.save(existing)).thenReturn(existing);
        when(authentication.getName()).thenReturn("admin@example.com");

        Page result = pageService.updatePage(id, updated);

        assertEquals("New Title", result.getTitle());
        verify(pageRepository).save(existing);
        verify(activityLogService).log(eq("UPDATE_PAGE"), anyString(), eq("admin@example.com"));
    }

    @Test
    void deletePage_ShouldDeleteAndLog() {
        Long id = 1L;
        when(authentication.getName()).thenReturn("admin@example.com");

        pageService.deletePage(id);

        verify(pageRepository).deleteById(id);
        verify(activityLogService).log(eq("DELETE_PAGE"), anyString(), eq("admin@example.com"));
    }
}
