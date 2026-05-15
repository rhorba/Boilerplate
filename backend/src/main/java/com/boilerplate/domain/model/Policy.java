package com.boilerplate.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "policies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Policy extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PolicyEffect effect;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PolicyResource resource;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PolicyAction action;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true,
        fetch = FetchType.EAGER)
    @Builder.Default
    private Set<PolicyCondition> conditions = new HashSet<>();

    public enum PolicyEffect {
        PERMIT, DENY
    }

    public enum PolicyResource {
        USER, GROUP, POLICY, AUDIT_LOG, SYSTEM
    }

    public enum PolicyAction {
        READ, CREATE, UPDATE, DELETE, MANAGE
    }
}
