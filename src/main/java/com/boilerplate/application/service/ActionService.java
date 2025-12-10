package com.boilerplate.application.service;

import com.boilerplate.domain.model.Action;
import com.boilerplate.domain.port.out.ActionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    public Action createAction(Action action) {
        return actionRepository.save(action);
    }

    public List<Action> getAllActions() {
        return actionRepository.findAll();
    }

    public void deleteAction(Long id) {
        actionRepository.deleteById(id);
    }
}
