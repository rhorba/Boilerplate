package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.User;
import com.boilerplate.domain.port.out.UserRepository;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataUserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final SpringDataUserRepository repository;

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(this::toDomain);
    }

    @Override
    public User save(User user) {
        UserEntity entity = toEntity(user);
        UserEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    private User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .role(entity.getRole() != null ? com.boilerplate.domain.model.Role.builder()
                        .id(entity.getRole().getId())
                        .name(entity.getRole().getName())
                        .build() : null)
                .groups(entity.getGroups() != null ? entity.getGroups().stream()
                        .map(g -> com.boilerplate.domain.model.UserGroup.builder()
                                .id(g.getId())
                                .name(g.getName())
                                .build())
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())
                .actions(entity.getActions() != null ? entity.getActions().stream()
                        .map(a -> com.boilerplate.domain.model.Action.builder()
                                .id(a.getId())
                                .name(a.getName())
                                .build())
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())
                .build();
    }

    @Override
    public java.util.List<User> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Optional<User> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .email(user.getEmail())
                .password(user.getPassword())
                .role(com.boilerplate.infrastructure.adapter.out.persistence.entity.RoleEntity.builder()
                        .id(user.getRole().getId())
                        .name(user.getRole().getName())
                        .build())
                .groups(user.getGroups() != null ? user.getGroups().stream()
                        .map(g -> com.boilerplate.infrastructure.adapter.out.persistence.entity.UserGroupEntity
                                .builder()
                                .id(g.getId())
                                .build())
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())
                .actions(user.getActions() != null ? user.getActions().stream()
                        .map(a -> com.boilerplate.infrastructure.adapter.out.persistence.entity.ActionEntity
                                .builder()
                                .id(a.getId())
                                .build())
                        .collect(java.util.stream.Collectors.toList()) : java.util.Collections.emptyList())
                .build();
    }
}
