package com.erp.qualitascareapi.security.domains;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.persistence.*;

@Entity
@Table(name="user_permission_overrides",
        indexes = @Index(name="idx_override_lookup",
                columnList = "tenant_id,user_id,resource,action,feature,priority"))
public class UserPermissionOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)
    private User user;

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
    private int priority = 100;

    private String reason;

    public UserPermissionOverride() {}

    public UserPermissionOverride(Long id) { this.id = id; }

    public UserPermissionOverride(Long id, User user, Tenant tenant, ResourceType resource, Action action,
                                  String feature, Effect effect, int priority, String reason) {
        this.id = id; this.user = user; this.tenant = tenant; this.resource = resource; this.action = action;
        this.feature = feature; this.effect = effect; this.priority = priority; this.reason = reason;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}

