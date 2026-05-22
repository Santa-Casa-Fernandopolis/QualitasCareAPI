package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.StatusArmazenamento;
import com.erp.qualitascareapi.pgrss.enums.TipoArmazenamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ArmazenamentoResiduoDto(
        Long id,
        Long tenantId,
        Long grupoId,
        String grupoNome,
        TipoArmazenamento tipoArmazenamento,
        LocalDateTime dataHoraEntrada,
        LocalDateTime dataHoraSaida,
        BigDecimal pesoEstimadoKg,
        String responsavelNome,
        String localizacao,
        StatusArmazenamento status,
        String observacoes
) {}
