package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.Role;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.RoleEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataRoleRepository;
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
class RoleRepositoryAdapterTest {

    @Mock
    private SpringDataRoleRepository repository;

    @InjectMocks
    private RoleRepositoryAdapter adapter;

    @Test
    void save_ShouldReturnRole() {
        Role role = Role.builder().name("ADMIN").build();
        RoleEntity entity = RoleEntity.builder().id(1L).name("ADMIN").build();
        when(repository.save(any(RoleEntity.class))).thenReturn(entity);

        Role result = adapter.save(role);

        assertEquals(1L, result.getId());
        assertEquals("ADMIN", result.getName());
    }

    @Test
    void findByName_ShouldReturnRole() {
        RoleEntity entity = RoleEntity.builder().id(1L).name("ADMIN").build();
        when(repository.findByName("ADMIN")).thenReturn(Optional.of(entity));

        Optional<Role> result = adapter.findByName("ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
    }

    @Test
    void findAll_ShouldReturnList() {
        RoleEntity entity = RoleEntity.builder().id(1L).name("ADMIN").build();
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
    void findById_ShouldReturnRole() {
        RoleEntity entity = RoleEntity.builder().id(1L).name("ADMIN").build();
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        Optional<Role> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }
}
