package com.boilerplate.application.service;

import com.boilerplate.application.dto.request.PolicyRequest;
import com.boilerplate.application.dto.response.PolicyResponse;
import com.boilerplate.application.mapper.PolicyMapper;
import com.boilerplate.domain.model.Policy;
import com.boilerplate.domain.model.Policy.PolicyAction;
import com.boilerplate.domain.model.Policy.PolicyEffect;
import com.boilerplate.domain.model.Policy.PolicyResource;
import com.boilerplate.domain.repository.PolicyRepository;
import com.boilerplate.presentation.exception.DuplicateResourceException;
import com.boilerplate.presentation.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @Mock
    private PolicyMapper policyMapper;

    @InjectMocks
    private PolicyService policyService;

    private Policy testPolicy;
    private PolicyResponse testPolicyResponse;

    @BeforeEach
    void setUp() {
        testPolicy = Policy.builder()
            .id(1L)
            .name("test-policy")
            .effect(PolicyEffect.PERMIT)
            .resource(PolicyResource.USER)
            .action(PolicyAction.READ)
            .enabled(true)
            .build();

        testPolicyResponse = new PolicyResponse(
            1L, "test-policy", null,
            PolicyEffect.PERMIT, PolicyResource.USER, PolicyAction.READ,
            true, Set.of(), null, null
        );
    }

    @Test
    void getAllPolicies_ReturnsAll() {
        when(policyRepository.findAll()).thenReturn(List.of(testPolicy));
        when(policyMapper.toResponse(testPolicy)).thenReturn(testPolicyResponse);

        List<PolicyResponse> result = policyService.getAllPolicies();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("test-policy");
    }

    @Test
    void getPolicyById_Found_ReturnsResponse() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(testPolicy));
        when(policyMapper.toResponse(testPolicy)).thenReturn(testPolicyResponse);

        PolicyResponse result = policyService.getPolicyById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getPolicyById_NotFound_ThrowsException() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.getPolicyById(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createPolicy_Success() {
        PolicyRequest request = new PolicyRequest(
            "new-policy", null, PolicyEffect.PERMIT,
            PolicyResource.USER, PolicyAction.READ, true, null
        );
        when(policyRepository.existsByName("new-policy")).thenReturn(false);
        when(policyMapper.toEntity(request)).thenReturn(testPolicy);
        when(policyRepository.save(testPolicy)).thenReturn(testPolicy);
        when(policyMapper.toResponse(testPolicy)).thenReturn(testPolicyResponse);

        PolicyResponse result = policyService.createPolicy(request);

        assertThat(result).isNotNull();
        verify(policyRepository).save(testPolicy);
    }

    @Test
    void createPolicy_DuplicateName_ThrowsException() {
        PolicyRequest request = new PolicyRequest(
            "test-policy", null, PolicyEffect.PERMIT,
            PolicyResource.USER, PolicyAction.READ, true, null
        );
        when(policyRepository.existsByName("test-policy")).thenReturn(true);

        assertThatThrownBy(() -> policyService.createPolicy(request))
            .isInstanceOf(DuplicateResourceException.class);

        verify(policyRepository, never()).save(any());
    }

    @Test
    void updatePolicy_SameName_Success() {
        PolicyRequest request = new PolicyRequest(
            "test-policy", "desc", PolicyEffect.DENY,
            PolicyResource.USER, PolicyAction.DELETE, true, null
        );
        when(policyRepository.findById(1L)).thenReturn(Optional.of(testPolicy));
        when(policyRepository.save(testPolicy)).thenReturn(testPolicy);
        when(policyMapper.toResponse(testPolicy)).thenReturn(testPolicyResponse);

        PolicyResponse result = policyService.updatePolicy(1L, request);

        assertThat(result).isNotNull();
        verify(policyMapper).updateEntity(testPolicy, request);
    }

    @Test
    void updatePolicy_NewName_Success() {
        PolicyRequest request = new PolicyRequest(
            "renamed-policy", null, PolicyEffect.DENY,
            PolicyResource.USER, PolicyAction.DELETE, true, null
        );
        when(policyRepository.findById(1L)).thenReturn(Optional.of(testPolicy));
        when(policyRepository.existsByName("renamed-policy")).thenReturn(false);
        when(policyRepository.save(testPolicy)).thenReturn(testPolicy);
        when(policyMapper.toResponse(testPolicy)).thenReturn(testPolicyResponse);

        PolicyResponse result = policyService.updatePolicy(1L, request);

        assertThat(result).isNotNull();
    }

    @Test
    void updatePolicy_DuplicateNewName_ThrowsException() {
        PolicyRequest request = new PolicyRequest(
            "other-policy", null, PolicyEffect.DENY,
            PolicyResource.USER, PolicyAction.DELETE, true, null
        );
        when(policyRepository.findById(1L)).thenReturn(Optional.of(testPolicy));
        when(policyRepository.existsByName("other-policy")).thenReturn(true);

        assertThatThrownBy(() -> policyService.updatePolicy(1L, request))
            .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    void updatePolicy_NotFound_ThrowsException() {
        PolicyRequest request = new PolicyRequest(
            "updated-policy", null, PolicyEffect.DENY,
            PolicyResource.USER, PolicyAction.DELETE, true, null
        );
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.updatePolicy(99L, request))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deletePolicy_Success() {
        when(policyRepository.findById(1L)).thenReturn(Optional.of(testPolicy));

        policyService.deletePolicy(1L);

        verify(policyRepository).delete(testPolicy);
    }

    @Test
    void deletePolicy_NotFound_ThrowsException() {
        when(policyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> policyService.deletePolicy(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
