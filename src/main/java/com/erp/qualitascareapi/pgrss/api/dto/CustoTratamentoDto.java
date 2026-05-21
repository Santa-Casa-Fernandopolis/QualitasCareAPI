package com.erp.qualitascareapi.pgrss.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CustoTratamentoDto(
        Long id,
        Long tenantId,
        Long empresaColetorId,
        String empresaColetorRazaoSocial,
        Long tipoResiduoId,
        String tipoResiduoNome,
        BigDecimal valorPorKg,
        String moeda,
        LocalDate vigenciaInicio,
        LocalDate vigenciaFim,
        String observacoes
) {}
