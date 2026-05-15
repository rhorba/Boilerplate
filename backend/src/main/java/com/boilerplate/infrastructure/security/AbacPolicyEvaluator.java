package com.boilerplate.infrastructure.security;

import com.boilerplate.domain.model.Policy;
import com.boilerplate.domain.model.Policy.PolicyAction;
import com.boilerplate.domain.model.Policy.PolicyEffect;
import com.boilerplate.domain.model.Policy.PolicyResource;
import com.boilerplate.domain.model.PolicyCondition;
import com.boilerplate.domain.model.PolicyCondition.ConditionOperator;
import com.boilerplate.domain.model.PolicyCondition.ConditionSubject;
import com.boilerplate.domain.model.UserAttribute;
import com.boilerplate.domain.repository.GroupRepository;
import com.boilerplate.domain.repository.PolicyRepository;
import com.boilerplate.domain.repository.UserAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Evaluates ABAC policies for a given subject (user), resource, and action.
 * Used from @PreAuthorize via Spring EL: @abacEvaluator.hasPermission(authentication, 'USER', 'READ')
 *
 * Decision logic (XACML-inspired):
 *   1. Collect all enabled policies matching resource+action.
 *   2. A DENY policy that matches all its conditions wins immediately.
 *   3. A PERMIT is granted if at least one PERMIT policy matches all its conditions.
 *   4. Default: DENY.
 */
@Component("abacEvaluator")
@RequiredArgsConstructor
public class AbacPolicyEvaluator {

    private final PolicyRepository policyRepository;
    private final UserAttributeRepository userAttributeRepository;
    private final GroupRepository groupRepository;

    @Transactional(readOnly = true)
    public boolean hasPermission(Authentication authentication, String resource, String action) {
        return evaluate(authentication, resource, action);
    }

    @Transactional(readOnly = true)
    public boolean hasPermission(Authentication authentication, String resource, String action, Object resourceId) {
        return evaluate(authentication, resource, action);
    }

    private boolean evaluate(Authentication authentication, String resource, String action) {
        if (authentication == null || !authentication.isAuthenticated()
            || "anonymousUser".equals(authentication.getPrincipal())) {
            return false;
        }

        PolicyResource policyResource;
        PolicyAction policyAction;
        try {
            policyResource = PolicyResource.valueOf(resource.toUpperCase(Locale.ROOT));
            policyAction = PolicyAction.valueOf(action.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return false;
        }

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long userId = principal.getUser().getId();
        Map<String, String> userAttrs = loadUserAttributes(userId);

        // Derive implicit group-membership attributes via repository (avoids lazy-loading the User entity)
        groupRepository.findGroupNamesByUserId(userId)
            .forEach(name -> userAttrs.putIfAbsent("group:" + name.toLowerCase(Locale.ROOT), "true"));

        List<Policy> candidates = policyRepository.findEnabledByResourceAndAction(policyResource, policyAction);

        // DENY wins if any deny policy fully matches
        boolean denied = candidates.stream()
            .filter(p -> p.getEffect() == PolicyEffect.DENY)
            .anyMatch(p -> allConditionsMatch(p, userAttrs));

        if (denied) {
            return false;
        }

        // PERMIT if any permit policy fully matches
        return candidates.stream()
            .filter(p -> p.getEffect() == PolicyEffect.PERMIT)
            .anyMatch(p -> allConditionsMatch(p, userAttrs));
    }

    private boolean allConditionsMatch(Policy policy, Map<String, String> userAttrs) {
        for (PolicyCondition condition : policy.getConditions()) {
            if (condition.getSubject() == ConditionSubject.USER) {
                String actual = userAttrs.get(condition.getAttributeKey());
                if (!evaluateCondition(actual, condition.getOperator(), condition.getAttributeValue())) {
                    return false;
                }
            }
            // RESOURCE and ENVIRONMENT subjects: extendable in future iterations
        }
        return true;
    }

    private boolean evaluateCondition(String actual, ConditionOperator operator, String expected) {
        if (actual == null) {
            return false;
        }
        return switch (operator) {
            case EQUALS -> actual.equalsIgnoreCase(expected);
            case NOT_EQUALS -> !actual.equalsIgnoreCase(expected);
            case IN -> Arrays.stream(expected.split(","))
                .map(String::trim)
                .anyMatch(v -> v.equalsIgnoreCase(actual));
            case NOT_IN -> Arrays.stream(expected.split(","))
                .map(String::trim)
                .noneMatch(v -> v.equalsIgnoreCase(actual));
            case CONTAINS -> actual.toLowerCase(Locale.ROOT).contains(expected.toLowerCase(Locale.ROOT));
            case STARTS_WITH -> actual.toLowerCase(Locale.ROOT).startsWith(
                expected.toLowerCase(Locale.ROOT));
        };
    }

    @Transactional(readOnly = true)
    public Set<String> computeEffectivePermissions(Long userId) {
        Map<String, String> userAttrs = loadUserAttributes(userId);
        groupRepository.findGroupNamesByUserId(userId)
            .forEach(name -> userAttrs.putIfAbsent("group:" + name.toLowerCase(Locale.ROOT), "true"));

        Set<String> effective = new HashSet<>();
        for (PolicyResource resource : PolicyResource.values()) {
            for (PolicyAction action : PolicyAction.values()) {
                if (isPermitted(resource, action, userAttrs)) {
                    effective.add(resource.name() + ":" + action.name());
                }
            }
        }
        return effective;
    }

    private boolean isPermitted(PolicyResource resource, PolicyAction action,
        Map<String, String> userAttrs) {
        List<Policy> candidates = policyRepository.findEnabledByResourceAndAction(resource, action);
        boolean denied = candidates.stream()
            .filter(p -> p.getEffect() == PolicyEffect.DENY)
            .anyMatch(p -> allConditionsMatch(p, userAttrs));
        if (denied) {
            return false;
        }
        return candidates.stream()
            .filter(p -> p.getEffect() == PolicyEffect.PERMIT)
            .anyMatch(p -> allConditionsMatch(p, userAttrs));
    }

    private Map<String, String> loadUserAttributes(Long userId) {
        return userAttributeRepository.findAllByUserId(userId).stream()
            .collect(Collectors.toMap(
                UserAttribute::getAttributeKey,
                UserAttribute::getAttributeValue,
                (existing, replacement) -> replacement
            ));
    }
}
