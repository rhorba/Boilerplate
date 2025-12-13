package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.ActivityLog;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.ActivityLogEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataActivityLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivityLogRepositoryAdapterTest {

    @Mock
    private SpringDataActivityLogRepository repository;

    @InjectMocks
    private ActivityLogRepositoryAdapter adapter;

    @Test
    void save_ShouldReturnLog() {
        ActivityLog log = ActivityLog.builder().action("LOGIN").timestamp(LocalDateTime.now()).build();
        ActivityLogEntity entity = ActivityLogEntity.builder().id(1L).action("LOGIN").timestamp(LocalDateTime.now())
                .build();
        when(repository.save(any(ActivityLogEntity.class))).thenReturn(entity);

        ActivityLog result = adapter.save(log);

        assertEquals(1L, result.getId());
        assertEquals("LOGIN", result.getAction());
    }

    @Test
    void findAll_ShouldReturnList() {
        ActivityLogEntity entity = ActivityLogEntity.builder().id(1L).build();
        when(repository.findAllByOrderByTimestampDesc()).thenReturn(Collections.singletonList(entity));

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void deleteAll_ShouldCallRepo() {
        doNothing().when(repository).deleteAll();
        adapter.deleteAll();
        verify(repository).deleteAll();
    }
}
