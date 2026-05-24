package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record CmeFluxoProcessoRequest(
        @NotNull Long tenantId,
        @NotBlank String nome,
        @NotNull TipoFluxoCME tipoFluxo,
        Integer numeroVersao,
        Boolean ativo,
        LocalDate dataVigenciaInicio,
        LocalDate dataVigenciaFim,
        String observacoes,
        List<CmeEtapaProcessoRequest> etapas
) {}
