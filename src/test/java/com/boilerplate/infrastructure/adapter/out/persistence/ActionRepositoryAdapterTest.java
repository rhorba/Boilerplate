package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.Action;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.ActionEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataActionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionRepositoryAdapterTest {

    @Mock
    private SpringDataActionRepository repository;

    @InjectMocks
    private ActionRepositoryAdapter adapter;

    @Test
    void save_ShouldReturnAction() {
        Action action = Action.builder().name("CREATE").build();
        ActionEntity entity = ActionEntity.builder().id(1L).name("CREATE").build();
        when(repository.save(any(ActionEntity.class))).thenReturn(entity);

        Action result = adapter.save(action);

        assertEquals(1L, result.getId());
        assertEquals("CREATE", result.getName());
    }

    @Test
    void findAll_ShouldReturnList() {
        ActionEntity entity = ActionEntity.builder().id(1L).name("CREATE").build();
        when(repository.findAll()).thenReturn(Collections.singletonList(entity));

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void deleteById_ShouldCallRepo() {
        doNothing().when(repository).deleteById(1L);
        adapter.deleteById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void findByName_ShouldReturnAction() {
        String name = "CREATE";
        ActionEntity entity = ActionEntity.builder().id(1L).name(name).build();
        when(repository.findByName(name)).thenReturn(java.util.Optional.of(entity));

        java.util.Optional<Action> result = adapter.findByName(name);

        assertEquals(name, result.get().getName());
    }

    @Test
    void findById_ShouldReturnAction() {
        Long id = 1L;
        ActionEntity entity = ActionEntity.builder().id(id).name("CREATE").build();
        when(repository.findById(id)).thenReturn(java.util.Optional.of(entity));

        java.util.Optional<Action> result = adapter.findById(id);

        assertEquals(id, result.get().getId());
    }
}
