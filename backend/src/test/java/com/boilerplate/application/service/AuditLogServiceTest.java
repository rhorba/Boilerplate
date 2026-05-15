package com.boilerplate.application.service;

import com.boilerplate.application.dto.response.AuditLogResponse;
import com.boilerplate.application.mapper.AuditLogMapper;
import com.boilerplate.domain.model.AuditLog;
import com.boilerplate.domain.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private AuditLogMapper auditLogMapper;

    @InjectMocks
    private AuditLogService auditLogService;

    private AuditLog testLog;
    private AuditLogResponse testLogResponse;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testLog = AuditLog.builder()
            .action("USER_LOGIN")
            .resource("User")
            .username("admin")
            .build();

        testLogResponse = AuditLogResponse.builder()
            .id(1L)
            .action("USER_LOGIN")
            .resource("User")
            .username("admin")
            .build();

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getAllAuditLogs_ReturnsPaginatedResults() {
        Page<AuditLog> page = new PageImpl<>(List.of(testLog), pageable, 1);
        when(auditLogRepository.findAll(pageable)).thenReturn(page);
        when(auditLogMapper.toResponse(testLog)).thenReturn(testLogResponse);

        Page<AuditLogResponse> result = auditLogService.getAllAuditLogs(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getAction()).isEqualTo("USER_LOGIN");
        verify(auditLogRepository).findAll(pageable);
    }

    @Test
    void getAllAuditLogs_EmptyRepository_ReturnsEmptyPage() {
        Page<AuditLog> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(auditLogRepository.findAll(pageable)).thenReturn(emptyPage);

        Page<AuditLogResponse> result = auditLogService.getAllAuditLogs(pageable);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        verify(auditLogMapper, never()).toResponse(any());
    }

    @Test
    void getAllAuditLogs_RespectsPageable() {
        Pageable customPageable = PageRequest.of(2, 5);
        Page<AuditLog> page = new PageImpl<>(List.of(), customPageable, 0);
        when(auditLogRepository.findAll(customPageable)).thenReturn(page);

        auditLogService.getAllAuditLogs(customPageable);

        verify(auditLogRepository).findAll(customPageable);
    }
}
