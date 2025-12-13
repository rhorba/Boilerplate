package com.boilerplate.application.service;

import com.boilerplate.domain.model.PageData;
import com.boilerplate.domain.port.out.PageDataRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PageDataServiceTest {

    @Mock
    private PageDataRepository pageDataRepository;
    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private PageDataService pageDataService;

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
    void createPageData_ShouldSaveAndLog() {
        PageData data = PageData.builder().pageId(1L).data("{}").build();
        when(pageDataRepository.save(data)).thenReturn(data);
        when(authentication.getName()).thenReturn("admin@example.com");

        PageData result = pageDataService.createPageData(data);

        assertEquals(data, result);
        verify(pageDataRepository).save(data);
        verify(activityLogService).log(eq("CREATE_PAGE_DATA"), anyString(), eq("admin@example.com"));
    }

    @Test
    void getAllDataByPageId_ShouldReturnList() {
        Long pageId = 1L;
        when(pageDataRepository.findAllByPageId(pageId)).thenReturn(List.of(PageData.builder().build()));

        List<PageData> result = pageDataService.getAllDataByPageId(pageId);

        assertEquals(1, result.size());
    }

    @Test
    void getPageDataById_ShouldReturnData() {
        Long id = 1L;
        PageData data = PageData.builder().id(id).build();
        when(pageDataRepository.findById(id)).thenReturn(Optional.of(data));

        Optional<PageData> result = pageDataService.getPageDataById(id);

        assertEquals(data, result.get());
    }

    @Test
    void updatePageData_ShouldUpdateAndLog() {
        Long id = 1L;
        PageData existing = PageData.builder().id(id).data("{}").build();
        PageData updated = PageData.builder().id(id).data("{\"new\":\"val\"}").build();

        when(pageDataRepository.findById(id)).thenReturn(Optional.of(existing));
        when(pageDataRepository.save(existing)).thenReturn(existing);
        when(authentication.getName()).thenReturn("admin@example.com");

        PageData result = pageDataService.updatePageData(id, updated);

        assertEquals("{\"new\":\"val\"}", result.getData());
        verify(pageDataRepository).save(existing);
        verify(activityLogService).log(eq("UPDATE_PAGE_DATA"), anyString(), eq("admin@example.com"));
    }

    @Test
    void updatePageData_ShouldThrowWhenNotFound() {
        Long id = 1L;
        PageData updated = PageData.builder().data("{}").build();
        when(pageDataRepository.findById(id)).thenReturn(Optional.empty());

        try {
            pageDataService.updatePageData(id, updated);
        } catch (RuntimeException e) {
            assertEquals("Page Data not found", e.getMessage());
        }
    }

    @Test
    void deletePageData_ShouldDeleteAndLog() {
        Long id = 1L;
        when(authentication.getName()).thenReturn("admin@example.com");

        pageDataService.deletePageData(id);

        verify(pageDataRepository).deleteById(id);
        verify(activityLogService).log(eq("DELETE_PAGE_DATA"), anyString(), eq("admin@example.com"));
    }
}
