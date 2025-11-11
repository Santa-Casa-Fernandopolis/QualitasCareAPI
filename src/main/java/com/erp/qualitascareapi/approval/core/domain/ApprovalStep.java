package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDecision;
import com.erp.qualitascareapi.approval.core.enums.ApprovalStepStatus;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "approval_steps",
        uniqueConstraints = @UniqueConstraint(name = "uq_step_request_order", columnNames = {"request_id", "stage_order"}),
        indexes = @Index(name = "idx_step_request_status", columnList = "request_id,status"))
public class ApprovalStep {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="request_id", nullable=false)
    private ApprovalRequest request;

    @Column(name = "stage_order", nullable = false)
    private Integer stageOrder;

    @Column(name = "stage_code", nullable = false, length = 80)
    private String stageCode;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=60)
    private OrgRoleType requiredRole;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="scope_setor_id")
    private Setor scopeSetor;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=20)
    private ApprovalStepStatus status = ApprovalStepStatus.PENDENTE;

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
