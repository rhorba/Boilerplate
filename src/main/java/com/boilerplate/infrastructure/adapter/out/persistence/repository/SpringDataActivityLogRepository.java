package com.boilerplate.infrastructure.adapter.out.persistence.repository;

import com.boilerplate.infrastructure.adapter.out.persistence.entity.ActivityLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataActivityLogRepository extends JpaRepository<ActivityLogEntity, Long> {
    List<ActivityLogEntity> findAllByOrderByTimestampDesc();
}
