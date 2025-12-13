package com.boilerplate.application.service;

import com.boilerplate.domain.model.Action;
import com.boilerplate.domain.port.out.ActionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActionServiceTest {

    @Mock
    private ActionRepository actionRepository;

    @InjectMocks
    private ActionService actionService;

    @Test
    void createAction_ShouldSaveAndReturnAction() {
        Action action = Action.builder().id(1L).name("TEST_ACTION").build();
        when(actionRepository.save(action)).thenReturn(action);

        Action result = actionService.createAction(action);

        assertEquals(action, result);
        verify(actionRepository).save(action);
    }

    @Test
    void getAllActions_ShouldReturnListOfActions() {
        Action action = Action.builder().id(1L).name("TEST_ACTION").build();
        when(actionRepository.findAll()).thenReturn(List.of(action));

        List<Action> result = actionService.getAllActions();

        assertEquals(1, result.size());
        assertEquals(action, result.get(0));
        verify(actionRepository).findAll();
    }

    @Test
    void deleteAction_ShouldDeleteAction() {
        Long id = 1L;
        actionService.deleteAction(id);
        verify(actionRepository).deleteById(id);
    }
}
