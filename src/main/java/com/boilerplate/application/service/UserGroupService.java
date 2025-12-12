package com.boilerplate.application.service;

import com.boilerplate.domain.model.UserGroup;
import com.boilerplate.domain.port.out.UserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@org.springframework.transaction.annotation.Transactional
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;
    private final ActivityLogService activityLogService;

    public UserGroup createGroup(UserGroup group) {
        UserGroup saved = userGroupRepository.save(group);
        String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        activityLogService.log("CREATE_GROUP", "Created group: " + saved.getName(), currentUserEmail);
        return saved;
    }

    public List<UserGroup> getAllGroups() {
        return userGroupRepository.findAll();
    }

    public Optional<UserGroup> getGroupById(Long id) {
        return userGroupRepository.findById(id);
    }

    public Optional<UserGroup> getGroupByName(String name) {
        return userGroupRepository.findByName(name);
    }

    public UserGroup updateGroup(Long id, UserGroup updatedGroup) {
        return userGroupRepository.findById(id).map(existing -> {
            existing.setName(updatedGroup.getName());
            existing.setDescription(updatedGroup.getDescription());
            // Update logic for users/pages if sent here
            // Note: Use dedicated endpoints for large scale membership management if needed
            if (updatedGroup.getUsers() != null) {
                // Mapping logic or rely on Repository Adapter to handle IDs if we pass full
                // objects
                existing.setUsers(updatedGroup.getUsers());
            }
            if (updatedGroup.getPages() != null) {
                existing.setPages(updatedGroup.getPages());
            }

            UserGroup saved = userGroupRepository.save(existing);
            String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext()
                    .getAuthentication().getName();
            activityLogService.log("UPDATE_GROUP", "Updated group: " + saved.getName(), currentUserEmail);
            return saved;
        }).orElseThrow(() -> new RuntimeException("Group not found"));
    }

    public void deleteGroup(Long id) {
        userGroupRepository.deleteById(id);
        String currentUserEmail = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication().getName();
        activityLogService.log("DELETE_GROUP", "Deleted group ID: " + id, currentUserEmail);
    }
}
