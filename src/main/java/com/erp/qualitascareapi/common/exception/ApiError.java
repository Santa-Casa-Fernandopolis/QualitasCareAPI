package com.erp.qualitascareapi.common.exception;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Representation of an error returned by the REST APIs.
 */
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        Map<String, Object> details
) {
    public ApiError {
        timestamp = timestamp != null ? timestamp : Instant.now();
        if (details == null || details.isEmpty()) {
            details = Collections.emptyMap();
        } else {
            details = Collections.unmodifiableMap(new LinkedHashMap<>(details));
        }
    }
}
