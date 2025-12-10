package com.boilerplate.infrastructure.adapter.out.persistence.repository;

import com.boilerplate.infrastructure.adapter.out.persistence.entity.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SpringDataPageRepository extends JpaRepository<PageEntity, Long> {
    Optional<PageEntity> findBySlug(String slug);
}
