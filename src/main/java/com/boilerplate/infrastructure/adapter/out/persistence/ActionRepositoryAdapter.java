package com.boilerplate.infrastructure.adapter.out.persistence;

import com.boilerplate.domain.model.Action;
import com.boilerplate.domain.port.out.ActionRepository;
import com.boilerplate.infrastructure.adapter.out.persistence.entity.ActionEntity;
import com.boilerplate.infrastructure.adapter.out.persistence.repository.SpringDataActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ActionRepositoryAdapter implements ActionRepository {

    private final SpringDataActionRepository repository;

    @Override
    public Action save(Action action) {
        ActionEntity entity = ActionEntity.builder()
                .id(action.getId())
                .name(action.getName())
                .build();
        ActionEntity saved = repository.save(entity);
        return mapToDomain(saved);
    }

    @Override
    public Optional<Action> findByName(String name) {
        return repository.findByName(name).map(this::mapToDomain);
    }

    @Override
    public List<Action> findAll() {
        return repository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public Optional<Action> findById(Long id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    private Action mapToDomain(ActionEntity entity) {
        return Action.builder()
                .id(entity.getId())
                .name(entity.getName())
                .build();
    }
}
