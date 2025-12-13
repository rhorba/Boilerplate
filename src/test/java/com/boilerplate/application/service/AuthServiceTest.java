package com.boilerplate.application.service;

import com.boilerplate.application.dto.AuthenticationRequest;
import com.boilerplate.application.dto.AuthenticationResponse;
import com.boilerplate.application.dto.RegisterRequest;
import com.boilerplate.domain.model.Role;
import com.boilerplate.domain.model.User;
import com.boilerplate.domain.port.out.RoleRepository;
import com.boilerplate.domain.port.out.UserRepository;
import com.boilerplate.infrastructure.config.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private ActivityLogService activityLogService;
    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_ShouldRegisterUser_WhenEmailDoesNotExist() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .password("password")
                .build();

        Role role = Role.builder().name("USER").build();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refreshToken");

        // Act
        AuthenticationResponse response = authService.register(request);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        assertEquals("refreshToken", response.getRefreshToken());
        verify(userRepository).save(any(User.class));
        verify(activityLogService).log(eq("REGISTER"), anyString(), eq(request.getEmail()));
    }

    @Test
    void register_ShouldThrowException_WhenEmailExists() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder().email("john@example.com").build();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenRoleNotFound() {
        // Arrange
        RegisterRequest request = RegisterRequest.builder()
                .email("john@example.com")
                .password("password")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> authService.register(request));
    }

    @Test
    void authenticate_ShouldAuthenticate_WhenCredentialsAreCorrect() {
        // Arrange
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("john@example.com")
                .password("password")
                .build();

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(request.getEmail())).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refreshToken");

        // Act
        AuthenticationResponse response = authService.authenticate(request);

        // Assert
        assertNotNull(response);
        assertEquals("accessToken", response.getAccessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(activityLogService).log(eq("LOGIN"), anyString(), eq(request.getEmail()));
    }
}
