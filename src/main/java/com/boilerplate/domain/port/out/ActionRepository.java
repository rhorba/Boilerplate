package com.boilerplate.domain.port.out;

import com.boilerplate.domain.model.Action;
import java.util.List;
import java.util.Optional;

public interface ActionRepository {
    Action save(Action action);

    Optional<Action> findByName(String name);

    List<Action> findAll();

    void deleteById(Long id);

    Optional<Action> findById(Long id);
}
