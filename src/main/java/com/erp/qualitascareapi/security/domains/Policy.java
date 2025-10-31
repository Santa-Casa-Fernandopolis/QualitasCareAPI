package com.erp.qualitascareapi.security.domains;

import com.erp.qualitascareapi.iam.domain.Tenant;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name="policies",
        indexes = {
                @Index(name="idx_policy_scope",   columnList="tenant_id,resource,action,feature,priority"),
                @Index(name="idx_policy_enabled", columnList="enabled")
        })
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private ResourceType resource;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private Action action;

    @Column(length=80) // NULL = coringa
    private String feature;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Effect effect;

    @Column(nullable=false)
    private boolean enabled = true;

    @Column(nullable=false)
    private int priority = 100;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="policy_roles",
            joinColumns=@JoinColumn(name="policy_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    @BatchSize(size=50)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy="policy", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
    @BatchSize(size=50)
    private List<PolicyCondition> conditions = new ArrayList<>();

    public Policy() {}

    public Policy(Long id) { this.id = id; }

    public Policy(Long id, Tenant tenant, ResourceType resource, Action action, String feature,
                  Effect effect, boolean enabled, int priority, String description) {
        this.id = id; this.tenant = tenant; this.resource = resource; this.action = action; this.feature = feature;
        this.effect = effect; this.enabled = enabled; this.priority = priority; this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public ResourceType getResource() { return resource; }
    public void setResource(ResourceType resource) { this.resource = resource; }
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }
    public Effect getEffect() { return effect; }
    public void setEffect(Effect effect) { this.effect = effect; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public List<PolicyCondition> getConditions() { return conditions; }
    public void setConditions(List<PolicyCondition> conditions) { this.conditions = conditions; }
}

