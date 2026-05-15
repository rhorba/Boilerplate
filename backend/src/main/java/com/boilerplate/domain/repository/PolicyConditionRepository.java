package com.boilerplate.domain.repository;

import com.boilerplate.domain.model.PolicyCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyConditionRepository extends JpaRepository<PolicyCondition, Long> {
}
