package com.erp.qualitascareapi.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Base runtime exception for domain and application level errors handled by the API.
 */
public class ApplicationException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;
    private final Map<String, Object> details;

    public ApplicationException(HttpStatus status, String errorCode, String message) {
        this(status, errorCode, message, null, null);
    }

    public ApplicationException(HttpStatus status, String errorCode, String message, Throwable cause) {
        this(status, errorCode, message, null, cause);
    }

    public ApplicationException(HttpStatus status, String errorCode, String message, Map<String, Object> details) {
        this(status, errorCode, message, details, null);
    }

    public ApplicationException(HttpStatus status, String errorCode, String message,
                                Map<String, Object> details, Throwable cause) {
        super(message, cause);
        this.status = Objects.requireNonNull(status, "status");
        this.errorCode = errorCode != null ? errorCode : status.name();
        if (details == null || details.isEmpty()) {
            this.details = Collections.emptyMap();
        } else {
            this.details = Collections.unmodifiableMap(new LinkedHashMap<>(details));
        }
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
