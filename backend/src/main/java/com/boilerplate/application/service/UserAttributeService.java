package com.boilerplate.application.service;

import com.boilerplate.application.dto.request.UserAttributeRequest;
import com.boilerplate.application.dto.response.UserAttributeResponse;
import com.boilerplate.application.mapper.UserAttributeMapper;
import com.boilerplate.domain.model.User;
import com.boilerplate.domain.model.UserAttribute;
import com.boilerplate.domain.repository.UserAttributeRepository;
import com.boilerplate.domain.repository.UserRepository;
import com.boilerplate.presentation.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAttributeService {

    private final UserAttributeRepository userAttributeRepository;
    private final UserRepository userRepository;
    private final UserAttributeMapper userAttributeMapper;

    @Transactional(readOnly = true)
    public List<UserAttributeResponse> getAttributesForUser(Long userId) {
        ensureUserExists(userId);
        return userAttributeRepository.findAllByUserId(userId).stream()
            .map(userAttributeMapper::toResponse)
            .toList();
    }

    @Transactional
    public UserAttributeResponse setUserAttribute(Long userId, UserAttributeRequest request) {
        User user = findUser(userId);

        UserAttribute attribute = userAttributeRepository
            .findByUserIdAndAttributeKey(userId, request.attributeKey())
            .orElseGet(() -> {
                UserAttribute newAttr = userAttributeMapper.toEntity(request);
                newAttr.setUser(user);
                return newAttr;
            });

        attribute.setAttributeValue(request.attributeValue());
        return userAttributeMapper.toResponse(userAttributeRepository.save(attribute));
    }

    @Transactional
    public void deleteUserAttribute(Long userId, String attributeKey) {
        ensureUserExists(userId);
        if (!userAttributeRepository.existsByUserIdAndAttributeKey(userId, attributeKey)) {
            throw new ResourceNotFoundException(
                "Attribute '" + attributeKey + "' not found for user id: " + userId
            );
        }
        userAttributeRepository.deleteByUserIdAndAttributeKey(userId, attributeKey);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private void ensureUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
    }
}
