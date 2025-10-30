package com.erp.qualitascareapi.security.domains;

import jakarta.persistence.*;

@Entity
@Table(
        name = "policy_conditions",
        indexes = {
                @Index(name = "idx_policy_condition_policy", columnList = "policy_id")
        }
)
public class PolicyCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_policy_condition_policy"))
    private Policy policy;

    // Renomeadas para evitar conflito com palavras reservadas (ex.: VALUE no H2)
    @Column(name = "cond_type", nullable = false, length = 80)
    private String type;        // TARGET_DEPARTMENT, USER_PROFESSION, TARGET_STATUS...

    @Column(name = "cond_operator", nullable = false, length = 20)
    private String operator;    // EQ, NE, IN, NOT_IN...

    @Column(name = "cond_value", nullable = false, length = 200)
    private String value;       // CURRENT_DEPT, "UTI|CME", "ABERTA|EM_EXECUCAO"...

    public PolicyCondition() {}

    public PolicyCondition(Long id) {
        this.id = id;
    }

    public PolicyCondition(Long id, Policy policy, String type, String operator, String value) {
        this.id = id;
        this.policy = policy;
        this.type = type;
        this.operator = operator;
        this.value = value;
    }

    // Getters / Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


