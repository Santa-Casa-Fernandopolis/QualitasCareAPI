package com.erp.qualitascareapi.security.app.target;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.Map;

/**
 * Exception thrown when the requested target cannot be found for authorization.
 */
public class TargetNotFoundException extends ApplicationException {

    public TargetNotFoundException(String targetType, Serializable identifier) {
        super(HttpStatus.NOT_FOUND,
                "target.not-found",
                String.format("Target %s with identifier %s was not found", targetType, identifier),
                Map.of("targetType", targetType, "identifier", identifier));
    }
}
