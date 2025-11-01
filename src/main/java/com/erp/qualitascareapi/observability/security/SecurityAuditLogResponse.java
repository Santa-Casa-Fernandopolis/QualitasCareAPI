package com.erp.qualitascareapi.observability.security;

import java.time.Instant;

public record SecurityAuditLogResponse(
        Long id,
        Instant timestamp,
        String username,
        SecurityAuditEventType eventType,
        String clientIp,
        String traceId,
        String description
) {
}
