package com.erp.qualitascareapi.pgrss.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustoTratamentoRequest(
        @NotNull Long tenantId,
        @NotNull Long grupoId,
        @NotNull @DecimalMin("0.0001") BigDecimal custoPorKg,
        @NotNull LocalDate dataInicioVigencia,
        LocalDate dataFimVigencia
) {}
