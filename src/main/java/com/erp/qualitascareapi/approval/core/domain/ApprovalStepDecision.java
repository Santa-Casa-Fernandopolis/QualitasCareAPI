package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDecision;
import com.erp.qualitascareapi.approval.core.enums.ApprovalStepStatus;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(name = "approval_step_decisions",
        indexes = {
                @Index(name = "idx_step_decision_request", columnList = "request_id,decided_at"),
                @Index(name = "idx_step_decision_step", columnList = "step_id")
        })
public class ApprovalStepDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private ApprovalRequest request;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "step_id")
    private ApprovalStep step;

    @Column(name = "from_stage_code", length = 80)
    private String fromStageCode;

    @Column(name = "to_stage_code", length = 80)
    private String toStageCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ApprovalDecision decision;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 30)
    private ApprovalStepStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 30)
    private ApprovalStepStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decided_by", nullable = false)
    private User decidedBy;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;

    @Column(length = 1200)
    private String comment;

    public ApprovalStepDecision() {
    }

    @PrePersist
    public void prePersist() {
        if (decidedAt == null) {
            decidedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ApprovalRequest getRequest() { return request; }
    public void setRequest(ApprovalRequest request) { this.request = request; }
    public ApprovalStep getStep() { return step; }
    public void setStep(ApprovalStep step) { this.step = step; }
    public String getFromStageCode() { return fromStageCode; }
    public void setFromStageCode(String fromStageCode) { this.fromStageCode = fromStageCode; }
    public String getToStageCode() { return toStageCode; }
    public void setToStageCode(String toStageCode) { this.toStageCode = toStageCode; }
    public ApprovalDecision getDecision() { return decision; }
    public void setDecision(ApprovalDecision decision) { this.decision = decision; }
    public ApprovalStepStatus getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(ApprovalStepStatus previousStatus) { this.previousStatus = previousStatus; }
    public ApprovalStepStatus getNewStatus() { return newStatus; }
    public void setNewStatus(ApprovalStepStatus newStatus) { this.newStatus = newStatus; }
    public User getDecidedBy() { return decidedBy; }
    public void setDecidedBy(User decidedBy) { this.decidedBy = decidedBy; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
