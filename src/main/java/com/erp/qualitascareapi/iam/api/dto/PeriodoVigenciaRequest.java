package com.erp.qualitascareapi.iam.api.dto;

import jakarta.validation.constraints.AssertTrue;

import java.time.Instant;

/**
 * Request payload describing the optional validity period for an assignment.
 */
public record PeriodoVigenciaRequest(
        Instant inicio,
        Instant fim
) {

    @AssertTrue(message = "vigenciaFim must be greater than or equal to vigenciaInicio")
    public boolean isRangeValid() {
        if (inicio == null || fim == null) {
            return true;
        }
        return !fim.isBefore(inicio);
    }
}
