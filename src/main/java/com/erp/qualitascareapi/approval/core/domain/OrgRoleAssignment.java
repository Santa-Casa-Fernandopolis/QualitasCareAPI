// org.domain
package com.erp.qualitascareapi.org.domain;

import com.erp.qualitascareapi.approval.core.enums.OrgRoleType;
import com.erp.qualitascareapi.core.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Entity @Audited
@Table(name="org_role_assignments",
        indexes = {
                @Index(name="idx_orgrole_tenant_role", columnList="tenant_id,roleType"),
                @Index(name="idx_orgrole_user", columnList="user_id")
        })
public class OrgRoleAssignment {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=60)
    private OrgRoleType roleType;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="setor_id")
    private Setor setor; // opcional (escopo setorial)

    private LocalDate validFrom;
    private LocalDate validUntil;
    private Boolean active = Boolean.TRUE;

    public OrgRoleAssignment() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public OrgRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(OrgRoleType roleType) {
        this.roleType = roleType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
