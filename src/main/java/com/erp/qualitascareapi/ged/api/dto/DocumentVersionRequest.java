package com.erp.qualitascareapi.ged.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DocumentVersionRequest(
        @NotNull @Min(0) Integer versaoMajor,
        @NotNull @Min(0) Integer versaoMinor,
        String resumoMudancas,
        LocalDate dataVigenciaInicio,
        LocalDate dataVigenciaFim
) {
}
