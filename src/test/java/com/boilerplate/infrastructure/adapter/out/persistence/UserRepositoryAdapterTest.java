package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.Role;
import com.boilerplate.domain.model.User;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.RoleEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
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
class UserRepositoryAdapterTest {

    @Mock
    private SpringDataUserRepository repository;

    @InjectMocks
    private UserRepositoryAdapter adapter;

    @Test
    void findByEmail_ShouldReturnUser() {
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .role(RoleEntity.builder().id(1L).name("USER").build())
                .build();
        when(repository.findByEmail("test@example.com")).thenReturn(Optional.of(entity));

        Optional<User> result = adapter.findByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
        assertEquals("USER", result.get().getRole().getName());
    }

    @Test
    void save_ShouldReturnUser() {
        User user = User.builder()
                .email("test@example.com")
                .role(Role.builder().id(1L).name("USER").build())
                .build();
        UserEntity entity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .role(RoleEntity.builder().id(1L).name("USER").build())
                .build();

        when(repository.save(any(UserEntity.class))).thenReturn(entity);

        User result = adapter.save(user);

        assertEquals(1L, result.getId());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void existsByEmail_ShouldReturnTrue() {
        when(repository.existsByEmail("test@example.com")).thenReturn(true);
        assertTrue(adapter.existsByEmail("test@example.com"));
    }

    @Test
    void findAll_ShouldReturnList() {
        UserEntity entity = UserEntity.builder().role(RoleEntity.builder().build()).build();
        when(repository.findAll()).thenReturn(Collections.singletonList(entity));

        assertEquals(1, adapter.findAll().size());
    }

    @Test
    void deleteById_ShouldCallRepository() {
        adapter.deleteById(1L);
        verify(repository).deleteById(1L);
    }
}
