package com.erp.qualitascareapi.observability.logging;

import java.time.Instant;
import java.util.Optional;

public record RequestLogFilter(
        Instant from,
        Instant to,
        String method,
        Integer status,
        String userId,
        String traceId,
        String path
) {
    public Optional<Instant> from() {
        return Optional.ofNullable(from);
    }

    public Optional<Instant> to() {
        return Optional.ofNullable(to);
    }

    public Optional<String> method() {
        return Optional.ofNullable(method).filter(s -> !s.isBlank());
    }

    public Optional<Integer> status() {
        return Optional.ofNullable(status);
    }

    public Optional<String> userId() {
        return Optional.ofNullable(userId).filter(s -> !s.isBlank());
    }

    public Optional<String> traceId() {
        return Optional.ofNullable(traceId).filter(s -> !s.isBlank());
    }

    public Optional<String> path() {
        return Optional.ofNullable(path).filter(s -> !s.isBlank());
    }
}
