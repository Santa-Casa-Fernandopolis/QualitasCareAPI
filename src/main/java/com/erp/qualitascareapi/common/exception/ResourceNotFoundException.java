package com.erp.qualitascareapi.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Exception thrown when a requested resource cannot be found.
 */
public class ResourceNotFoundException extends ApplicationException {

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super(HttpStatus.NOT_FOUND, "resource.not-found",
                String.format("%s with identifier %s was not found", resourceName, identifier),
                Map.of("resource", resourceName, "identifier", identifier));
    }

    public ResourceNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, "resource.not-found", message);
    }
}
