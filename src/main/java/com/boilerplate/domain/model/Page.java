package com.boilerplate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    private Long id;
    private String title;
    private String slug;
    private String content;
    private String icon;
    private String roles; // Comma separated roles, e.g. "USER,ADMIN"
    private String schema; // JSON string defining the fields for this page
    private String accessControl; // JSON string defining permissions
}
