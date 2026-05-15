package com.boilerplate.application.service;

import com.boilerplate.application.dto.request.UserAttributeRequest;
import com.boilerplate.application.dto.response.UserAttributeResponse;
import com.boilerplate.application.mapper.UserAttributeMapper;
import com.boilerplate.domain.model.User;
import com.boilerplate.domain.model.UserAttribute;
import com.boilerplate.domain.repository.UserAttributeRepository;
import com.boilerplate.domain.repository.UserRepository;
import com.boilerplate.presentation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserAttributeServiceTest {

    @Mock
    private UserAttributeRepository userAttributeRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAttributeMapper userAttributeMapper;

    @InjectMocks
    private UserAttributeService userAttributeService;

    private User testUser;
    private UserAttribute testAttribute;
    private UserAttributeResponse testAttributeResponse;

    @BeforeEach
    void setUp() {
        testUser = User.builder().id(1L).username("testuser").build();
        testAttribute = new UserAttribute();
        testAttributeResponse = new UserAttributeResponse(1L, "dept", "engineering");
    }

    @Test
    void getAttributesForUser_UserExists_ReturnsAttributes() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userAttributeRepository.findAllByUserId(1L)).thenReturn(List.of(testAttribute));
        when(userAttributeMapper.toResponse(testAttribute)).thenReturn(testAttributeResponse);

        List<UserAttributeResponse> result = userAttributeService.getAttributesForUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).attributeKey()).isEqualTo("dept");
    }

    @Test
    void getAttributesForUser_UserNotFound_ThrowsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userAttributeService.getAttributesForUser(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void setUserAttribute_NewAttribute_CreatesAndSaves() {
        UserAttributeRequest request = new UserAttributeRequest("dept", "engineering");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userAttributeRepository.findByUserIdAndAttributeKey(1L, "dept"))
            .thenReturn(Optional.empty());
        when(userAttributeMapper.toEntity(request)).thenReturn(testAttribute);
        when(userAttributeRepository.save(testAttribute)).thenReturn(testAttribute);
        when(userAttributeMapper.toResponse(testAttribute)).thenReturn(testAttributeResponse);

        UserAttributeResponse result = userAttributeService.setUserAttribute(1L, request);

        assertThat(result).isNotNull();
        verify(userAttributeRepository).save(testAttribute);
    }

    @Test
    void setUserAttribute_ExistingAttribute_UpdatesValue() {
        UserAttributeRequest request = new UserAttributeRequest("dept", "finance");
        UserAttribute existing = new UserAttribute();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userAttributeRepository.findByUserIdAndAttributeKey(1L, "dept"))
            .thenReturn(Optional.of(existing));
        when(userAttributeRepository.save(existing)).thenReturn(existing);
        when(userAttributeMapper.toResponse(existing)).thenReturn(testAttributeResponse);

        userAttributeService.setUserAttribute(1L, request);

        verify(userAttributeRepository).save(existing);
        verify(userAttributeMapper, never()).toEntity(any());
    }

    @Test
    void setUserAttribute_UserNotFound_ThrowsException() {
        UserAttributeRequest request = new UserAttributeRequest("dept", "engineering");
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userAttributeService.setUserAttribute(99L, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteUserAttribute_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userAttributeRepository.existsByUserIdAndAttributeKey(1L, "dept")).thenReturn(true);

        userAttributeService.deleteUserAttribute(1L, "dept");

        verify(userAttributeRepository).deleteByUserIdAndAttributeKey(1L, "dept");
    }

    @Test
    void deleteUserAttribute_AttributeNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(userAttributeRepository.existsByUserIdAndAttributeKey(1L, "dept")).thenReturn(false);

        assertThatThrownBy(() -> userAttributeService.deleteUserAttribute(1L, "dept"))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteUserAttribute_UserNotFound_ThrowsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userAttributeService.deleteUserAttribute(99L, "dept"))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
