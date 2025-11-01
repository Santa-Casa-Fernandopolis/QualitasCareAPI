package com.erp.qualitascareapi.observability.security;

import java.time.Instant;
import java.util.Optional;

public record SecurityAuditEventFilter(
        Instant from,
        Instant to,
        String username,
        SecurityAuditEventType eventType,
        String traceId
) {
    public Optional<Instant> from() {
        return Optional.ofNullable(from);
    }

    public Optional<Instant> to() {
        return Optional.ofNullable(to);
    }

    public Optional<String> username() {
        return Optional.ofNullable(username).filter(s -> !s.isBlank());
    }

    public Optional<SecurityAuditEventType> eventType() {
        return Optional.ofNullable(eventType);
    }

    public Optional<String> traceId() {
        return Optional.ofNullable(traceId).filter(s -> !s.isBlank());
    }
}
