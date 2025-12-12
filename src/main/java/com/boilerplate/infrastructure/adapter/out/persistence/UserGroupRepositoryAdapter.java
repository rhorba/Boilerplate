package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.Page;
import com.boilerplate.domain.model.User;
import com.boilerplate.domain.model.UserGroup;
import com.boilerplate.domain.port.out.UserGroupRepository;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.PageEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.UserGroupEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataUserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserGroupRepositoryAdapter implements UserGroupRepository {

    private final SpringDataUserGroupRepository repository;

    @Override
    public UserGroup save(UserGroup userGroup) {
        UserGroupEntity entity = toEntity(userGroup);
        UserGroupEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public Optional<UserGroup> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<UserGroup> findByName(String name) {
        return repository.findByName(name).map(this::toDomain);
    }

    @Override
    public List<UserGroup> findAll() {
        return repository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private UserGroupEntity toEntity(UserGroup domain) {
        return UserGroupEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .description(domain.getDescription())
                // Relationships are handled via ID usually or assume they are managed by
                // Service separately to avoid cyclic mapping issues
                // For simplicity, we might skip mapping full User/Page lists back to entity
                // here if strictly for updates,
                // but if we want to save associations, we need them.
                // NOTE: Mapping full deep structures can be tricky. Let's keep it simple for
                // now or fetch existing refs.
                .build();
    }

    private UserGroup toDomain(UserGroupEntity entity) {
        return UserGroup.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .users(entity.getUsers() != null
                        ? entity.getUsers().stream().map(this::toUserDomainSummary).collect(Collectors.toList())
                        : Collections.emptyList())
                .pages(entity.getPages() != null
                        ? entity.getPages().stream().map(this::toPageDomainSummary).collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    // Shallow mapping to avoid infinite recursion
    private User toUserDomainSummary(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .firstname(entity.getFirstname())
                .lastname(entity.getLastname())
                .build();
    }

    private Page toPageDomainSummary(PageEntity entity) {
        return Page.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .build();
    }
}
