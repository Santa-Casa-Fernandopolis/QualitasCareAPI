package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.TipoDestinoFinal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ColetaExternaRequest(
        @NotNull Long tenantId,
        @NotNull Long empresaId,
        @NotNull Long grupoId,
        @NotNull LocalDate dataColeta,
        @NotNull @DecimalMin("0.001") BigDecimal pesoTotalKg,
        @NotNull TipoDestinoFinal destinacao,
        String numeroManifesto,
        String placaVeiculo,
        String nomeMotorista,
        @NotBlank String responsavelNome,
        String observacoes
) {}
