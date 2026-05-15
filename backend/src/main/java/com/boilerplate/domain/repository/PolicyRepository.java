package com.boilerplate.domain.repository;

import com.boilerplate.domain.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Optional<Policy> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT p FROM Policy p LEFT JOIN FETCH p.conditions"
        + " WHERE p.resource = :resource AND p.action = :action AND p.enabled = true")
    List<Policy> findEnabledByResourceAndAction(
        @Param("resource") Policy.PolicyResource resource,
        @Param("action") Policy.PolicyAction action
    );

    @Query("SELECT p FROM Policy p LEFT JOIN FETCH p.conditions WHERE p.enabled = true")
    List<Policy> findAllEnabled();
}
