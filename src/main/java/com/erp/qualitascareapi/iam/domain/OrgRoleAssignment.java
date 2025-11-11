package com.erp.qualitascareapi.iam.domain;

import com.erp.qualitascareapi.common.vo.PeriodoVigencia;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "iam_org_role_assignments",
        uniqueConstraints = @UniqueConstraint(name = "uq_orgrole_assignment",
                columnNames = {"tenant_id", "role_type", "user_id", "setor_id", "vigencia_inicio"}),
        indexes = {
                @Index(name = "ix_orgrole_tenant_role_active", columnList = "tenant_id,role_type,active"),
                @Index(name = "ix_orgrole_user", columnList = "user_id")
        })
public class OrgRoleAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false, length = 60)
    private OrgRoleType roleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private Setor setor;

    @Embedded
    private PeriodoVigencia vigencia;

    @Column(nullable = false)
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

    public PeriodoVigencia getVigencia() {
        return vigencia;
    }

    public void setVigencia(PeriodoVigencia vigencia) {
        this.vigencia = vigencia;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
