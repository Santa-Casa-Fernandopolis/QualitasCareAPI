package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity @Audited
@Table(name="approval_audit_log", indexes=@Index(name="idx_log_request", columnList="request_id"))
public class ApprovalAuditLog {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="request_id", nullable=false)
    private ApprovalRequest request;

    private Integer stepOrder;

    @Column(nullable=false, length=60)
    private String event;

    @Column(nullable=false)
    private LocalDateTime whenOccurred;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="who_user_id")
    private User who;

    @Column(length=1000)
    private String data;

    public ApprovalAuditLog() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ApprovalRequest getRequest() { return request; }
    public void setRequest(ApprovalRequest request) { this.request = request; }
    public Integer getStepOrder() { return stepOrder; }
    public void setStepOrder(Integer stepOrder) { this.stepOrder = stepOrder; }
    public String getEvent() { return event; }
    public void setEvent(String event) { this.event = event; }
    public LocalDateTime getWhenOccurred() { return whenOccurred; }
    public void setWhenOccurred(LocalDateTime whenOccurred) { this.whenOccurred = whenOccurred; }
    public User getWho() { return who; }
    public void setWho(User who) { this.who = who; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
}
