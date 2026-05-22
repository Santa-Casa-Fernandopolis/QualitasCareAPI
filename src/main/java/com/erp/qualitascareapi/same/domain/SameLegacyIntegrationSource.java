package com.erp.qualitascareapi.same.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "same_legacy_integration_sources",
        uniqueConstraints = @UniqueConstraint(name = "uq_same_legacy_source_tenant_system_name",
                columnNames = {"tenant_id", "source_system", "name"}))
public class SameLegacyIntegrationSource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source_system", nullable = false, length = 30)
    private SameSourceSystem sourceSystem;

    @NotBlank
    @Column(name = "jdbc_url", nullable = false, length = 500)
    private String jdbcUrl;

    @Column(length = 120)
    private String username;

    @Column(name = "encrypted_password", length = 500)
    private String encryptedPassword;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public SameSourceSystem getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(SameSourceSystem sourceSystem) { this.sourceSystem = sourceSystem; }
    public String getJdbcUrl() { return jdbcUrl; }
    public void setJdbcUrl(String jdbcUrl) { this.jdbcUrl = jdbcUrl; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEncryptedPassword() { return encryptedPassword; }
    public void setEncryptedPassword(String encryptedPassword) { this.encryptedPassword = encryptedPassword; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
