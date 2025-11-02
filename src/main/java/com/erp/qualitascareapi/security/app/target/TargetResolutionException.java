package com.erp.qualitascareapi.security.app.target;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

/**
 * Exception thrown when the application cannot resolve a target for authorization evaluation.
 */
public class TargetResolutionException extends ApplicationException {

    public TargetResolutionException(String message) {
        super(HttpStatus.BAD_REQUEST, "target.resolution-error", message);
    }

    public TargetResolutionException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, "target.resolution-error", message, null, cause);
    }
}
