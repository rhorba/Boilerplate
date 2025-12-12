package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.ActivityLogService;
import com.boilerplate.domain.model.ActivityLog;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/activity-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ActivityLogController {

    private final ActivityLogService service;

    @GetMapping
    public ResponseEntity<List<ActivityLog>> getAllLogs() {
        return ResponseEntity.ok(service.getAllLogs());
    }

    @DeleteMapping
    public ResponseEntity<Void> clearLogs() {
        service.clearLogs();
        return ResponseEntity.noContent().build();
    }
}
