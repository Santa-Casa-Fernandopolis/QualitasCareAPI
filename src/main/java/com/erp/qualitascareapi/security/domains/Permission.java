package com.erp.qualitascareapi.security.domains;

import com.erp.qualitascareapi.iam.domain.Tenant;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.persistence.*;

@Entity
@Table(name="permissions",
        uniqueConstraints = {
                @UniqueConstraint(name="uq_perm_scope", columnNames={"tenant_id","resource","action","feature"}),
                @UniqueConstraint(name="uq_perm_code_tenant", columnNames={"tenant_id","code"})
        })
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private ResourceType resource;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=30)
    private Action action;

    @Column(length=80) // NULL = coringa
    private String feature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Column(length=120)
    private String code; // ex: "NC_READ@LISTA"

    public Permission() {}

    public Permission(Long id) { this.id = id; }

    public Permission(Long id, ResourceType resource, Action action, String feature, Tenant tenant, String code) {
        this.id = id; this.resource = resource; this.action = action; this.feature = feature; this.tenant = tenant; this.code = code;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ResourceType getResource() { return resource; }
    public void setResource(ResourceType resource) { this.resource = resource; }
    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }
    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}

