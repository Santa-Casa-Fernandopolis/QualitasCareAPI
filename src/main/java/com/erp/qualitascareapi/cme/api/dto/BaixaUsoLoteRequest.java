package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record BaixaUsoLoteRequest(
        @NotNull Long tenantId,
        @NotBlank String codigoLote,
        LocalDateTime dataHora,
        Long responsavelId,
        @NotNull ResultadoConformidade resultadoMarcadorQuimicoInterno,
        String observacoes
) {}
