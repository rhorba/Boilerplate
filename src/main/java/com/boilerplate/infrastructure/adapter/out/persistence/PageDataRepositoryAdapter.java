package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.PageData;
import com.boilerplate.domain.port.out.PageDataRepository;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.PageDataEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataPageDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PageDataRepositoryAdapter implements PageDataRepository {

    private final SpringDataPageDataRepository repository;

    @Override
    public PageData save(PageData pageData) {
        PageDataEntity entity = mapToEntity(pageData);
        PageDataEntity savedEntity = repository.save(entity);
        return mapToDomain(savedEntity);
    }

    @Override
    public List<PageData> findAllByPageId(Long pageId) {
        return repository.findAllByPageId(pageId).stream()
                .map(this::mapToDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PageData> findById(Long id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    private PageDataEntity mapToEntity(PageData domain) {
        return PageDataEntity.builder()
                .id(domain.getId())
                .pageId(domain.getPageId())
                .data(domain.getData())
                .build();
    }

    private PageData mapToDomain(PageDataEntity entity) {
        return PageData.builder()
                .id(entity.getId())
                .pageId(entity.getPageId())
                .data(entity.getData())
                .build();
    }
}
