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
    public Optional<Instant> fromOptional() {
        return Optional.ofNullable(from);
    }

    public Optional<Instant> toOptional() {
        return Optional.ofNullable(to);
    }

    public Optional<String> methodOptional() {
        return Optional.ofNullable(method).filter(s -> !s.isBlank());
    }

    public Optional<Integer> statusOptional() {
        return Optional.ofNullable(status);
    }

    public Optional<String> userIdOptional() {
        return Optional.ofNullable(userId).filter(s -> !s.isBlank());
    }

    public Optional<String> traceIdOptional() {
        return Optional.ofNullable(traceId).filter(s -> !s.isBlank());
    }

    public Optional<String> pathOptional() {
        return Optional.ofNullable(path).filter(s -> !s.isBlank());
    }
}
