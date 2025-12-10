package com.boilerplate.infrastructure.adapter.out.persistence.repository;

import com.boilerplate.infrastructure.adapter.out.persistence.entity.PageDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataPageDataRepository extends JpaRepository<PageDataEntity, Long> {
    List<PageDataEntity> findAllByPageId(Long pageId);
}
