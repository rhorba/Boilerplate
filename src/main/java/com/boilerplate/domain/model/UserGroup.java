package com.boilerplate.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGroup {
    private Long id;
    private String name;
    private String description;
    private List<User> users;
    private List<Page> pages;
}
