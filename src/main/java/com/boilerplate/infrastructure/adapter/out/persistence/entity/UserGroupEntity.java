package com.boilerplate.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_groups")
public class UserGroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "groups")
    private List<UserEntity> users;

    @ManyToMany
    @JoinTable(name = "group_pages", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "page_id"))
    private List<PageEntity> pages;
}
