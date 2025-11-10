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
    // getters/setters …
}
