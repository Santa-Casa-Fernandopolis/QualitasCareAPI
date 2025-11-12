package com.erp.qualitascareapi.iam.api.dto;

import java.time.Instant;

/**
 * Data transfer object representing the validity period embedded in assignments.
 */
public record PeriodoVigenciaDto(
        Instant inicio,
        Instant fim
) {
}
