package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.approval.core.enums.ApprovalRequestStatus;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "approval_requests",
        indexes = {
                @Index(name = "ix_request_tenant_domain_status", columnList = "tenant_id,domain,status"),
                @Index(name = "ix_request_tenant_target", columnList = "tenant_id,target_key"),
                @Index(name = "ix_request_requested_at", columnList = "requested_at")
        })
public class ApprovalRequest {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=60)
    private ApprovalDomain domain;

    @Column(name = "target_key", nullable = false, length = 200)
    private String targetKey;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="requested_by", nullable=false)
    private User requestedBy;

    @Column(nullable=false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30)
    private ApprovalRequestStatus status = ApprovalRequestStatus.ABERTA;

    @Column(length = 200)
    private String flowNameSnapshot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scope_setor_id")
    private Setor scopeSetor;

    public ApprovalRequest() {}

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

    public String getTargetKey() {
        return targetKey;
    }

    public void setTargetKey(String targetKey) {
        this.targetKey = targetKey;
    }

    public User getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(User requestedBy) {
        this.requestedBy = requestedBy;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public ApprovalRequestStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalRequestStatus status) {
        this.status = status;
    }

    public String getFlowNameSnapshot() {
        return flowNameSnapshot;
    }

    public void setFlowNameSnapshot(String flowNameSnapshot) {
        this.flowNameSnapshot = flowNameSnapshot;
    }

    public Setor getScopeSetor() {
        return scopeSetor;
    }

    public void setScopeSetor(Setor scopeSetor) {
        this.scopeSetor = scopeSetor;
    }
}
