package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDecision;
import com.erp.qualitascareapi.approval.core.enums.ApprovalStepStatus;
import com.erp.qualitascareapi.approval.core.enums.OrgRoleType;
import com.erp.qualitascareapi.core.domain.Setor;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity @Audited
@Table(name="approval_steps",
        indexes = @Index(name="idx_step_request_order", columnList="request_id,stageOrder"))
public class ApprovalStep {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="request_id", nullable=false)
    private ApprovalRequest request;

    @Column(name="stageOrder", nullable=false)
    private Integer stageOrder;

    @Column(nullable=false, length=80)
    private String stageCode;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=60)
    private OrgRoleType requiredRole;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="scope_setor_id")
    private Setor scopeSetor;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private ApprovalStepStatus status = ApprovalStepStatus.PENDING;

    @Enumerated(EnumType.STRING) @Column(length=20)
    private ApprovalDecision decision;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="decided_by")
    private User decidedBy;

    private LocalDateTime decidedAt;

    @Column(length=500)
    private String comment;

    @Column(nullable=false)
    private Integer approvalsCount = 0;

    public ApprovalStep() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
