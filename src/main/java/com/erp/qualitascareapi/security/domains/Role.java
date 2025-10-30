package com.erp.qualitascareapi.security.domains;

import jakarta.persistence.*;

@Entity
@Table(name = "roles",
        uniqueConstraints = @UniqueConstraint(name = "uq_role_tenant_name",
                columnNames = {"tenant_id","name"}))
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String name; // ENFERMEIRO, TECNICO, MEDICO, ADMIN_QUALIDADE...

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    private String description;

    public Role() {}

    public Role(Long id) { this.id = id; }

    public Role(Long id, String name, Tenant tenant, String description) {
        this.id = id; this.name = name; this.tenant = tenant; this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

