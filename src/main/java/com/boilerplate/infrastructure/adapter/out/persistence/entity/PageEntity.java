package com.boilerplate.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pages")
public class PageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String icon;

    private String roles;

    @Column(columnDefinition = "TEXT")
    private String schema; // JSON string defining the fields for this page

    @Column(columnDefinition = "TEXT")
    private String accessControl;

    @ManyToMany(mappedBy = "pages", fetch = FetchType.EAGER)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({ "pages", "users", "owner" })
    private java.util.List<UserGroupEntity> groups;
}
