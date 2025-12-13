package com.boilerplate.application.service;

import com.boilerplate.domain.model.Role;
import com.boilerplate.domain.model.User;
import com.boilerplate.domain.port.out.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private UserService userService;

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
    void getAllUsers_ShouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(User.builder().build()));
        List<User> result = userService.getAllUsers();
        assertEquals(1, result.size());
    }

    @Test
    void updateUser_ShouldUpdateAndLog() {
        Long id = 1L;
        User existing = User.builder().id(id).email("old@example.com").build();
        User updated = User.builder().id(id)
                .firstname("New")
                .lastname("Name")
                .email("new@example.com")
                .role(Role.builder().name("ADMIN").build())
                .password("newpass")
                .build();

        when(userRepository.findById(id)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);
        when(passwordEncoder.encode("newpass")).thenReturn("encoded");
        when(authentication.getName()).thenReturn("admin@example.com");

        User result = userService.updateUser(id, updated);

        assertEquals("New", result.getFirstname());
        assertEquals("encoded", result.getPassword());
        verify(userRepository).save(existing);
        verify(activityLogService).log(eq("UPDATE_USER"), anyString(), eq("admin@example.com"));
    }

    @Test
    void deleteUser_ShouldDeleteAndLog() {
        Long id = 1L;
        when(authentication.getName()).thenReturn("admin@example.com");

        userService.deleteUser(id);

        verify(userRepository).deleteById(id);
        verify(activityLogService).log(eq("DELETE_USER"), anyString(), eq("admin@example.com"));
    }
}
