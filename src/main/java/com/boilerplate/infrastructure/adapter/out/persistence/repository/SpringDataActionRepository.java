package com.boilerplate.infrastructure.adapter.out.persistence.repository;

import com.boilerplate.infrastructure.adapter.out.persistence.entity.ActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataActionRepository extends JpaRepository<ActionEntity, Long> {
    Optional<ActionEntity> findByName(String name);
}
