package com.erp.qualitascareapi.observability.logging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "request_logs",
        indexes = {
                @Index(name = "idx_request_logs_ts", columnList = "logged_at"),
                @Index(name = "idx_request_logs_user", columnList = "user_id"),
                @Index(name = "idx_request_logs_trace", columnList = "trace_id")
        })
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "logged_at", nullable = false)
    private Instant timestamp;

    @Column(nullable = false, length = 10)
    private String method;

    @Column(nullable = false, length = 255)
    private String path;

    @Column(nullable = false)
    private int status;

    @Column(nullable = false)
    private long durationMs;

    @Column(name = "trace_id", length = 64)
    private String traceId;

    @Column(name = "user_id", length = 120)
    private String userId;

    @Column(name = "client_ip", length = 64)
    private String clientIp;

    @Column(name = "http_version", length = 16)
    private String httpVersion;

    @Column(name = "content_length")
    private Long contentLength;

    protected RequestLog() {
    }

    public RequestLog(Instant timestamp, String method, String path, int status, long durationMs,
                      String traceId, String userId, String clientIp, String httpVersion, Long contentLength) {
        this.timestamp = timestamp;
        this.method = method;
        this.path = path;
        this.status = status;
        this.durationMs = durationMs;
        this.traceId = traceId;
        this.userId = userId;
        this.clientIp = clientIp;
        this.httpVersion = httpVersion;
        this.contentLength = contentLength;
    }

    public Long getId() {
        return id;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public int getStatus() {
        return status;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getTraceId() {
        return traceId;
    }

    public String getUserId() {
        return userId;
    }

    public String getClientIp() {
        return clientIp;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Long getContentLength() {
        return contentLength;
    }
}
