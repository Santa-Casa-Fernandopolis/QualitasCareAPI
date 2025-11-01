package com.erp.qualitascareapi.observability.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "security_audit_events",
        indexes = {
                @Index(name = "idx_sec_audit_ts", columnList = "occurred_at"),
                @Index(name = "idx_sec_audit_user", columnList = "username"),
                @Index(name = "idx_sec_audit_type", columnList = "event_type")
        })
public class SecurityAuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "occurred_at", nullable = false)
    private Instant timestamp;

    @Column(nullable = false, length = 120)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 40)
    private SecurityAuditEventType eventType;

    @Column(name = "client_ip", length = 64)
    private String clientIp;

    @Column(name = "trace_id", length = 64)
    private String traceId;

    @Column(length = 255)
    private String description;

    protected SecurityAuditEvent() {
    }

    public SecurityAuditEvent(Instant timestamp, String username, SecurityAuditEventType eventType,
                              String clientIp, String traceId, String description) {
        this.timestamp = timestamp;
        this.username = username;
        this.eventType = eventType;
        this.clientIp = clientIp;
        this.traceId = traceId;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }

    public SecurityAuditEventType getEventType() {
        return eventType;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getDescription() {
        return description;
    }
}
