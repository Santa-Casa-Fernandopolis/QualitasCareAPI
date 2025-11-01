package com.erp.qualitascareapi.observability.logging.dto;

import java.time.Instant;

public record RequestLogResponse(
        Long id,
        Instant timestamp,
        String method,
        String path,
        int status,
        long durationMs,
        String traceId,
        String userId,
        String clientIp,
        String httpVersion,
        Long contentLength
) {
}
