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
    public Optional<Instant> fromOptional() {
        return Optional.ofNullable(from);
    }

    public Optional<Instant> toOptional() {
        return Optional.ofNullable(to);
    }

    public Optional<String> usernameOptional() {
        return Optional.ofNullable(username).filter(s -> !s.isBlank());
    }

    public Optional<SecurityAuditEventType> eventTypeOptional() {
        return Optional.ofNullable(eventType);
    }

    public Optional<String> traceIdOptional() {
        return Optional.ofNullable(traceId).filter(s -> !s.isBlank());
    }
}
