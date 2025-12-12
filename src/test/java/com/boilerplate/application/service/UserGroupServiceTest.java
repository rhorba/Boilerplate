package com.boilerplate.application.service;

import com.boilerplate.domain.model.UserGroup;
import com.boilerplate.domain.port.out.UserGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGroupServiceTest {

    @Mock
    private UserGroupRepository userGroupRepository;

    @Mock
    private ActivityLogService activityLogService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserGroupService userGroupService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void createGroup_ShouldSaveAndLog() {
        UserGroup group = UserGroup.builder().name("Test Group").build();
        when(userGroupRepository.save(any(UserGroup.class))).thenReturn(group);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");

        UserGroup created = userGroupService.createGroup(group);

        assertNotNull(created);
        assertEquals("Test Group", created.getName());
        verify(userGroupRepository).save(group);
        verify(activityLogService).log(eq("CREATE_GROUP"), anyString(), eq("admin@test.com"));
    }

    @Test
    void getAllGroups_ShouldReturnList() {
        when(userGroupRepository.findAll()).thenReturn(List.of(UserGroup.builder().name("G1").build()));

        List<UserGroup> groups = userGroupService.getAllGroups();

        assertFalse(groups.isEmpty());
        assertEquals(1, groups.size());
    }

    @Test
    void updateGroup_ShouldUpdateFields() {
        Long id = 1L;
        UserGroup existing = UserGroup.builder().id(id).name("Old Name").build();
        UserGroup update = UserGroup.builder().name("New Name").description("Desc").build();

        when(userGroupRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userGroupRepository.save(any(UserGroup.class))).thenAnswer(i -> i.getArgument(0));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");

        UserGroup result = userGroupService.updateGroup(id, update);

        assertEquals("New Name", result.getName());
        assertEquals("Desc", result.getDescription());
        verify(activityLogService).log(eq("UPDATE_GROUP"), anyString(), eq("admin@test.com"));
    }
}
