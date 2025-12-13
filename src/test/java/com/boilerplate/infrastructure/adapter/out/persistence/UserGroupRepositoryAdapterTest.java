package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.UserGroup;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.UserGroupEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataUserGroupRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserGroupRepositoryAdapterTest {

    @Mock
    private SpringDataUserGroupRepository repository;

    @InjectMocks
    private UserGroupRepositoryAdapter adapter;

    @Test
    void save_ShouldReturnGroup() {
        UserGroup group = UserGroup.builder().name("Test Group").build();
        UserGroupEntity entity = UserGroupEntity.builder().id(1L).name("Test Group").build();

        when(repository.save(any(UserGroupEntity.class))).thenReturn(entity);

        UserGroup result = adapter.save(group);

        assertEquals(1L, result.getId());
        assertEquals("Test Group", result.getName());
    }

    @Test
    void findById_ShouldReturnGroup() {
        UserGroupEntity entity = UserGroupEntity.builder().id(1L).name("Test Group").build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<UserGroup> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Test Group", result.get().getName());
    }

    @Test
    void findByName_ShouldReturnGroup() {
        UserGroupEntity entity = UserGroupEntity.builder().id(1L).name("Test Group").build();
        when(repository.findByName("Test Group")).thenReturn(Optional.of(entity));

        Optional<UserGroup> result = adapter.findByName("Test Group");

        assertTrue(result.isPresent());
        assertEquals("Test Group", result.get().getName());
    }

    @Test
    void findAll_ShouldReturnList() {
        UserGroupEntity entity = UserGroupEntity.builder().id(1L).name("Test Group").build();
        when(repository.findAll()).thenReturn(Collections.singletonList(entity));

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void deleteById_ShouldCallRepo() {
        adapter.deleteById(1L);
        verify(repository).deleteById(1L);
    }
}
