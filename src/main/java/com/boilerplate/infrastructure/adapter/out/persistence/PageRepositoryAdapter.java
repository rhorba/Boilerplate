package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.Page;
import com.boilerplate.domain.port.out.PageRepository;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.PageEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PageRepositoryAdapter implements PageRepository {

    private final SpringDataPageRepository repository;

    @Override
    public Page save(Page page) {
        PageEntity entity = mapToEntity(page);
        PageEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Page> findBySlug(String slug) {
        return repository.findBySlug(slug).map(this::mapToDomain);
    }

    @Override
    public List<Page> findAll() {
        return repository.findAll().stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Page> findById(Long id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    private PageEntity mapToEntity(Page page) {
        return PageEntity.builder()
                .id(page.getId())
                .title(page.getTitle())
                .slug(page.getSlug())
                .content(page.getContent())
                .icon(page.getIcon())
                .roles(page.getRoles())
                .schema(page.getSchema())
                .accessControl(page.getAccessControl())
                .groups(page.getGroups() != null ? page.getGroups().stream()
                        .map(g -> com.boilerplate.infrastructure.adapter.out.persistence.entity.UserGroupEntity
                                .builder()
                                .id(g.getId())
                                .name(g.getName())
                                .description(g.getDescription())
                                // We avoid mapping users/pages recursively to prevent infinite loops
                                .build())
                        .collect(Collectors.toList()) : null)
                .build();
    }

    private Page mapToDomain(PageEntity entity) {
        return Page.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .slug(entity.getSlug())
                .content(entity.getContent())
                .icon(entity.getIcon())
                .roles(entity.getRoles())
                .schema(entity.getSchema())
                .accessControl(entity.getAccessControl())
                .groups(entity.getGroups() != null ? entity.getGroups().stream()
                        .map(g -> com.boilerplate.domain.model.UserGroup.builder()
                                .id(g.getId())
                                .name(g.getName())
                                .description(g.getDescription())
                                .build())
                        .collect(Collectors.toList()) : java.util.Collections.emptyList())
                .build();
    }
}
