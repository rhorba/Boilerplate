package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.UserGroupService;
import com.boilerplate.domain.model.UserGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user-groups")
@RequiredArgsConstructor
public class UserGroupController {

    private final UserGroupService userGroupService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<UserGroup>> getAllGroups() {
        return ResponseEntity.ok(userGroupService.getAllGroups());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserGroup> getGroupById(@PathVariable Long id) {
        return userGroupService.getGroupById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserGroup> createGroup(@RequestBody UserGroup group) {
        return ResponseEntity.ok(userGroupService.createGroup(group));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserGroup> updateGroup(@PathVariable Long id, @RequestBody UserGroup group) {
        return ResponseEntity.ok(userGroupService.updateGroup(id, group));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        userGroupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }
}
