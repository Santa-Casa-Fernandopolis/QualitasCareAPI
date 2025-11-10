// approval.core.domain
package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity @Audited
@Table(name="approval_flow_defs",
        uniqueConstraints = @UniqueConstraint(name="uq_flow_tenant_domain_name", columnNames={"tenant_id","domain","name"}))
public class ApprovalFlowDef {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=60)
    private ApprovalDomain domain;

    @Column(nullable=false, length=120)
    private String name;

    @Column(nullable=false)
    private Boolean active = Boolean.TRUE;

    public ApprovalFlowDef() {}

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

    public ApprovalDomain getDomain() {
        return domain;
    }

    public void setDomain(ApprovalDomain domain) {
        this.domain = domain;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
