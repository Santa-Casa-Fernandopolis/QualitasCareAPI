package com.erp.qualitascareapi.pgrss.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ColetaInternaRequest(
        @NotNull Long tenantId,
        @NotNull LocalDateTime dataHoraColeta,
        String nomeRota,
        @NotBlank String responsavelNome,
        String observacoes
) {}
