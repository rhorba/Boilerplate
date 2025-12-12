package com.boilerplate.application.service;

import com.boilerplate.domain.model.ActivityLog;
import com.boilerplate.domain.port.out.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository repository;

    public void log(String action, String description, String userEmail) {
        ActivityLog log = ActivityLog.builder()
                .action(action)
                .description(description)
                .userEmail(userEmail)
                .timestamp(LocalDateTime.now())
                .build();
        repository.save(log);
    }

    public List<ActivityLog> getAllLogs() {
        return repository.findAll();
    }

    public void clearLogs() {
        repository.deleteAll();
    }
}
