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

    @Column(name = "return_to_stage_code", length = 80)
    private String returnToStageCode;

    @Column(nullable=false)
    private Integer approvalsCount = 0;

    public ApprovalStep() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApprovalRequest getRequest() { return request; }
    public void setRequest(ApprovalRequest request) { this.request = request; }
    public Integer getStageOrder() { return stageOrder; }
    public void setStageOrder(Integer stageOrder) { this.stageOrder = stageOrder; }
    public String getStageCode() { return stageCode; }
    public void setStageCode(String stageCode) { this.stageCode = stageCode; }
    public OrgRoleType getRequiredRole() { return requiredRole; }
    public void setRequiredRole(OrgRoleType requiredRole) { this.requiredRole = requiredRole; }
    public Setor getScopeSetor() { return scopeSetor; }
    public void setScopeSetor(Setor scopeSetor) { this.scopeSetor = scopeSetor; }
    public ApprovalStepStatus getStatus() { return status; }
    public void setStatus(ApprovalStepStatus status) { this.status = status; }
    public ApprovalDecision getDecision() { return decision; }
    public void setDecision(ApprovalDecision decision) { this.decision = decision; }
    public User getDecidedBy() { return decidedBy; }
    public void setDecidedBy(User decidedBy) { this.decidedBy = decidedBy; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getReturnToStageCode() { return returnToStageCode; }
    public void setReturnToStageCode(String returnToStageCode) { this.returnToStageCode = returnToStageCode; }
    public Integer getApprovalsCount() { return approvalsCount; }
    public void setApprovalsCount(Integer approvalsCount) { this.approvalsCount = approvalsCount; }
}
