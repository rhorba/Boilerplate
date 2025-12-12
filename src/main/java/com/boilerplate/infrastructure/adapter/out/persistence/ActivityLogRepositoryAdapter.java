package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.ActivityLog;
import com.boilerplate.domain.port.out.ActivityLogRepository;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.ActivityLogEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActivityLogRepositoryAdapter implements ActivityLogRepository {

    private final SpringDataActivityLogRepository repository;

    @Override
    public ActivityLog save(ActivityLog log) {
        ActivityLogEntity entity = ActivityLogEntity.builder()
                .action(log.getAction())
                .description(log.getDescription())
                .userEmail(log.getUserEmail())
                .timestamp(log.getTimestamp())
                .build();
        return mapToDomain(repository.save(entity));
    }

    @Override
    public List<ActivityLog> findAll() {
        return repository.findAllByOrderByTimestampDesc().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    private ActivityLog mapToDomain(ActivityLogEntity entity) {
        return ActivityLog.builder()
                .id(entity.getId())
                .action(entity.getAction())
                .description(entity.getDescription())
                .userEmail(entity.getUserEmail())
                .timestamp(entity.getTimestamp())
                .build();
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
