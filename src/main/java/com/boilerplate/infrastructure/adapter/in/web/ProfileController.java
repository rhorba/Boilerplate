package com.boilerplate.infrastructure.adapter.in.web;

import com.boilerplate.application.service.UserService;
import com.boilerplate.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return userService.getUserByEmail(userDetails.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateMyProfile(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody User updatedUser) {
        // Enforce that we are updating the logged-in user
        // We need to find the ID of the current user first.
        return userService.getUserByEmail(userDetails.getUsername())
                .map(existingUser -> userService.updateUser(existingUser.getId(), updatedUser))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
