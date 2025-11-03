package com.erp.qualitascareapi.cme.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AutoclaveRequest(@NotNull Long tenantId,
                               @NotBlank String nome,
                               String fabricante,
                               String modelo,
                               String numeroSerie,
                               String localizacao,
                               LocalDate ultimaHigienizacaoProfunda,
                               Boolean ativo) {
}
