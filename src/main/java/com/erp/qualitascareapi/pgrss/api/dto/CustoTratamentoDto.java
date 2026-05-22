package com.erp.qualitascareapi.pgrss.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustoTratamentoDto(
        Long id,
        Long tenantId,
        Long grupoId,
        String grupoNome,
        BigDecimal custoPorKg,
        LocalDate dataInicioVigencia,
        LocalDate dataFimVigencia,
        Boolean ativo
) {}
