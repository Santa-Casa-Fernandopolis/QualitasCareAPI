package com.erp.qualitascareapi.security.domains;

import jakarta.persistence.*;

@Entity
@Table(name="role_permissions",
        uniqueConstraints = @UniqueConstraint(name="uq_role_perm",
                columnNames={"tenant_id","role_id","permission_id"}))
public class RolePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id", nullable=false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="permission_id", nullable=false)
    private Permission permission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    public RolePermission() {}

    public RolePermission(Long id) { this.id = id; }

    public RolePermission(Long id, Role role, Permission permission, Tenant tenant) {
        this.id = id; this.role = role; this.permission = permission; this.tenant = tenant;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
}
