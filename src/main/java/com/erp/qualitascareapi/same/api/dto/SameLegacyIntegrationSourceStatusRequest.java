package com.erp.qualitascareapi.same.api.dto;

import jakarta.validation.constraints.NotNull;

public record SameLegacyIntegrationSourceStatusRequest(@NotNull Boolean active) {
}
