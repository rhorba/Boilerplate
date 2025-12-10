package com.boilerplate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {
    private Long id;
    private String action; // e.g., "LOGIN", "CREATE_PAGE"
    private String description; // e.g., "User admin logged in"
    private String userEmail;
    private LocalDateTime timestamp;
}
