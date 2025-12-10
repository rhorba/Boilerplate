package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.ActionService;
import com.boilerplate.domain.model.Action;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/actions")
@RequiredArgsConstructor
public class ActionController {

    private final ActionService actionService;

    @GetMapping
    public ResponseEntity<List<Action>> getAllActions() {
        return ResponseEntity.ok(actionService.getAllActions());
    }

    @PostMapping
    public ResponseEntity<Action> createAction(@RequestBody Action action) {
        return ResponseEntity.ok(actionService.createAction(action));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAction(@PathVariable Long id) {
        actionService.deleteAction(id);
        return ResponseEntity.noContent().build();
    }
}
