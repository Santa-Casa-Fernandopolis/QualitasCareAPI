package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record CmeFluxoProcessoDto(
        Long id,
        Long tenantId,
        String nome,
        TipoFluxoCME tipoFluxo,
        Integer numeroVersao,
        boolean ativo,
        LocalDate dataVigenciaInicio,
        LocalDate dataVigenciaFim,
        String observacoes,
        LocalDateTime criadoEm,
        List<CmeEtapaProcessoDto> etapas
) {}
