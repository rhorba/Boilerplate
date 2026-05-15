package com.boilerplate.application.service;

import com.boilerplate.application.dto.request.UpdateProfileRequest;
import com.boilerplate.application.dto.response.UserProfileResponse;
import com.boilerplate.application.mapper.UserProfileMapper;
import com.boilerplate.domain.model.User;
import com.boilerplate.domain.model.UserProfile;
import com.boilerplate.domain.repository.UserProfileRepository;
import com.boilerplate.domain.repository.UserRepository;
import com.boilerplate.presentation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @InjectMocks
    private UserProfileService userProfileService;

    private User testUser;
    private UserProfile testProfile;
    private UserProfileResponse testProfileResponse;
    private UpdateProfileRequest updateRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .password("encodedPassword")
            .build();

        testProfile = UserProfile.builder()
            .id(1L)
            .user(testUser)
            .bio("Test bio")
            .phoneNumber("+1234567890")
            .build();

        testProfileResponse = UserProfileResponse.builder()
            .bio("Test bio")
            .phoneNumber("+1234567890")
            .build();

        updateRequest = UpdateProfileRequest.builder()
            .firstName("John")
            .lastName("Doe")
            .bio("Updated bio")
            .phoneNumber("+0987654321")
            .build();
    }

    @Test
    void getProfile_Success() {
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(userProfileMapper.toResponse(testProfile)).thenReturn(testProfileResponse);

        UserProfileResponse result = userProfileService.getProfile(1L);

        assertThat(result).isNotNull();
        assertThat(result.getBio()).isEqualTo("Test bio");
        verify(userProfileRepository).findByUserId(1L);
        verify(userProfileMapper).toResponse(testProfile);
    }

    @Test
    void getProfile_NotFound_ThrowsException() {
        when(userProfileRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.getProfile(99L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("User profile not found");

        verify(userProfileMapper, never()).toResponse(any());
    }

    @Test
    void upsertProfile_ExistingProfile_UpdatesAndReturns() {
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.of(testProfile));
        when(userProfileRepository.save(testProfile)).thenReturn(testProfile);
        when(userProfileMapper.toResponse(testProfile)).thenReturn(testProfileResponse);

        UserProfileResponse result = userProfileService.upsertProfile(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(userProfileMapper).updateEntityFromRequest(updateRequest, testProfile);
        verify(userProfileRepository).save(testProfile);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void upsertProfile_NoExistingProfile_CreatesNewAndReturns() {
        when(userProfileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userProfileRepository.save(any(UserProfile.class))).thenReturn(testProfile);
        when(userProfileMapper.toResponse(testProfile)).thenReturn(testProfileResponse);

        UserProfileResponse result = userProfileService.upsertProfile(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(userRepository).findById(1L);
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    void upsertProfile_UserNotFound_ThrowsException() {
        when(userProfileRepository.findByUserId(99L)).thenReturn(Optional.empty());
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userProfileService.upsertProfile(99L, updateRequest))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessageContaining("User not found");

        verify(userProfileRepository, never()).save(any());
    }
}
