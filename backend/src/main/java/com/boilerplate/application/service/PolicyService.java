package com.boilerplate.application.service;

import com.boilerplate.application.dto.request.PolicyRequest;
import com.boilerplate.application.dto.response.PolicyResponse;
import com.boilerplate.application.mapper.PolicyMapper;
import com.boilerplate.domain.model.Policy;
import com.boilerplate.domain.model.PolicyCondition;
import com.boilerplate.domain.repository.PolicyRepository;
import com.boilerplate.presentation.exception.DuplicateResourceException;
import com.boilerplate.presentation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final PolicyMapper policyMapper;

    @Transactional(readOnly = true)
    public List<PolicyResponse> getAllPolicies() {
        return policyRepository.findAll().stream()
            .map(policyMapper::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public PolicyResponse getPolicyById(Long id) {
        return policyMapper.toResponse(findById(id));
    }

    @Transactional
    public PolicyResponse createPolicy(PolicyRequest request) {
        if (policyRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Policy already exists with name: " + request.name());
        }

        Policy policy = policyMapper.toEntity(request);

        if (request.conditions() != null) {
            request.conditions().forEach(condReq -> {
                PolicyCondition condition = policyMapper.toConditionEntity(condReq);
                condition.setPolicy(policy);
                policy.getConditions().add(condition);
            });
        }

        return policyMapper.toResponse(policyRepository.save(policy));
    }

    @Transactional
    public PolicyResponse updatePolicy(Long id, PolicyRequest request) {
        Policy policy = findById(id);

        if (!policy.getName().equals(request.name()) && policyRepository.existsByName(request.name())) {
            throw new DuplicateResourceException("Policy already exists with name: " + request.name());
        }

        policyMapper.updateEntity(policy, request);

        policy.getConditions().clear();
        if (request.conditions() != null) {
            request.conditions().forEach(condReq -> {
                PolicyCondition condition = policyMapper.toConditionEntity(condReq);
                condition.setPolicy(policy);
                policy.getConditions().add(condition);
            });
        }

        return policyMapper.toResponse(policyRepository.save(policy));
    }

    @Transactional
    public void deletePolicy(Long id) {
        policyRepository.delete(findById(id));
    }

    private Policy findById(Long id) {
        return policyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Policy not found with id: " + id));
    }
}
