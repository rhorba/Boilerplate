package com.boilerplate.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "policy_conditions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PolicyCondition extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConditionSubject subject;

    @Column(name = "attribute_key", nullable = false, length = 100)
    private String attributeKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ConditionOperator operator;

    @Column(name = "attribute_value", nullable = false, length = 255)
    private String attributeValue;

    public enum ConditionSubject {
        USER, RESOURCE, ENVIRONMENT
    }

    public enum ConditionOperator {
        EQUALS, NOT_EQUALS, IN, NOT_IN, CONTAINS, STARTS_WITH
    }
}
