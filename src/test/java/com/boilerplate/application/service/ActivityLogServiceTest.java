package com.boilerplate.application.service;

import com.boilerplate.domain.model.ActivityLog;
import com.boilerplate.domain.port.out.ActivityLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivityLogServiceTest {

    @Mock
    private ActivityLogRepository repository;

    @InjectMocks
    private ActivityLogService service;

    @Test
    void log_ShouldSaveLog() {
        String action = "TEST_ACTION";
        String description = "Test Description";
        String email = "test@example.com";

        service.log(action, description, email);

        ArgumentCaptor<ActivityLog> captor = ArgumentCaptor.forClass(ActivityLog.class);
        verify(repository).save(captor.capture());
        ActivityLog captured = captor.getValue();

        assertEquals(action, captured.getAction());
        assertEquals(description, captured.getDescription());
        assertEquals(email, captured.getUserEmail());
        assertNotNull(captured.getTimestamp());
    }

    @Test
    void getAllLogs_ShouldReturnLogs() {
        when(repository.findAll()).thenReturn(List.of(ActivityLog.builder().build()));
        List<ActivityLog> logs = service.getAllLogs();
        assertEquals(1, logs.size());
    }

    @Test
    void clearLogs_ShouldDeleteAll() {
        service.clearLogs();
        verify(repository).deleteAll();
    }
}
