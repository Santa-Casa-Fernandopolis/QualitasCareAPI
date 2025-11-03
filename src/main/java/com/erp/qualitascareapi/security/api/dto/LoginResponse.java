package com.erp.qualitascareapi.security.api.dto;

import java.time.Instant;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Instant expiresAt
) {
}
