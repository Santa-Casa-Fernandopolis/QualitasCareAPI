package com.erp.qualitascareapi.security.domains;

import jakarta.persistence.*;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code; // SCF, SCJ...

    @Column(nullable = false)
    private String name;

    private boolean active = true;

    public Tenant() {}

    public Tenant(Long id) { this.id = id; }

    public Tenant(Long id, String code, String name, boolean active) {
        this.id = id; this.code = code; this.name = name; this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

