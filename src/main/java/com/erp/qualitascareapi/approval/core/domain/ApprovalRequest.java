package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.approval.core.enums.ApprovalRequestStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity @Audited
@Table(name="approval_requests",
        indexes = {
                @Index(name="idx_request_tenant_domain_target", columnList="tenant_id,domain,targetKey"),
                @Index(name="idx_request_status", columnList="status")
        })
public class ApprovalRequest {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="tenant_id", nullable=false)
    private Tenant tenant;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=60)
    private ApprovalDomain domain;

    @Column(nullable=false, length=200)
    private String targetKey;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="requested_by", nullable=false)
    private User requestedBy;

    @Column(nullable=false)
    private LocalDateTime requestedAt;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=30)
    private ApprovalRequestStatus status = ApprovalRequestStatus.OPEN;

    @Column(length=200)
    private String flowNameSnapshot;

    public ApprovalRequest() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
