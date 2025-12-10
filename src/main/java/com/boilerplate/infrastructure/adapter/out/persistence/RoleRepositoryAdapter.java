package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.Role;
import com.boilerplate.domain.port.out.RoleRepository;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.RoleEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final SpringDataRoleRepository repository;

    @Override
    public Role save(Role role) {
        RoleEntity entity = RoleEntity.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
        RoleEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return repository.findByName(name).map(this::mapToDomain);
    }

    @Override
    public List<Role> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    private Role mapToDomain(RoleEntity entity) {
        return Role.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
