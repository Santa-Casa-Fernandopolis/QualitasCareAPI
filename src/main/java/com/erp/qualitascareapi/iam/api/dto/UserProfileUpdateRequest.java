package com.erp.qualitascareapi.iam.api.dto;

import jakarta.validation.constraints.Size;

public record UserProfileUpdateRequest(
        String fullName,
        String department,
        @Size(min = 6) String password
) {
}
