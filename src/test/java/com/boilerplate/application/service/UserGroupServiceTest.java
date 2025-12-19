package com.boilerplate.application.service;

import com.boilerplate.application.service.UserService;
import com.boilerplate.domain.model.UserGroup;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGroupServiceTest {

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserGroupService userGroupService;

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
    void createGroup_ShouldSaveAndLog() {
        UserGroup group = UserGroup.builder().name("Test Group").build();
        when(userGroupRepository.save(group)).thenReturn(group);
        when(authentication.getName()).thenReturn("admin@example.com");
        when(userService.getUserByEmail("admin@example.com")).thenReturn(Optional.empty());

        UserGroup result = userGroupService.createGroup(group);

        assertEquals(group, result);
        verify(userGroupRepository).save(group);
        verify(activityLogService).log(eq("CREATE_GROUP"), anyString(), eq("admin@example.com"));
    }

    @Test
    void deleteGroup_ShouldDeleteAndLog() {
        Long id = 1L;
        when(authentication.getName()).thenReturn("admin@example.com");

        userGroupService.deleteGroup(id);

        verify(userGroupRepository).deleteById(id);
        verify(activityLogService).log(eq("DELETE_GROUP"), anyString(), eq("admin@example.com"));
    }
}
