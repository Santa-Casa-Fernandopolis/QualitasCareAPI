package com.erp.qualitascareapi.pgrss.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustoTratamentoRequest(
        @NotNull Long tenantId,
        @NotNull Long empresaColetorId,
        @NotNull Long tipoResiduoId,
        @NotNull @DecimalMin("0.0001") BigDecimal valorPorKg,
        String moeda,
        @NotNull LocalDate vigenciaInicio,
        LocalDate vigenciaFim,
        String observacoes
) {}
