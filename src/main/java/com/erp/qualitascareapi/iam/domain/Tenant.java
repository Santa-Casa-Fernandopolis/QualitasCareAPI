package com.erp.qualitascareapi.iam.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 14)
    private String cnpj;

    @Column(length = 255)
    private String logo;

    private boolean active = true;

    public Tenant() {}

    public Tenant(Long id) { this.id = id; }

    public Tenant(Long id, String code, String name, String cnpj, String logo, boolean active) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.cnpj = cnpj;
        this.logo = logo;
        this.active = active;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}

