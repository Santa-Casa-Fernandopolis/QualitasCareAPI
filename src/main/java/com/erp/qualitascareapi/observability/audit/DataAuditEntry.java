package com.erp.qualitascareapi.observability.audit;

import java.time.Instant;
import java.util.Map;

public record DataAuditEntry(
        Long revisionId,
        Instant timestamp,
        String username,
        String clientIp,
        Map<String, Object> state
) {
}
