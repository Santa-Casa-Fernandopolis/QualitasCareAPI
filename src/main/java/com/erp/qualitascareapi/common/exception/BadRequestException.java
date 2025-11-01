package com.erp.qualitascareapi.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/**
 * Exception representing invalid client requests (HTTP 400).
 */
public class BadRequestException extends ApplicationException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, "request.invalid", message);
    }

    public BadRequestException(String message, Map<String, Object> details) {
        super(HttpStatus.BAD_REQUEST, "request.invalid", message, details);
    }
}
