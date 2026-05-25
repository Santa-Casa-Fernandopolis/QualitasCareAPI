package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import com.erp.qualitascareapi.cme.enums.CmeTipoEquipamento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record CmeEquipamentoRequest(
        Long tenantId,
        @NotBlank String codigo,
        @NotBlank String nome,
        @NotNull CmeTipoEquipamento tipoEquipamento,
        Set<CmeEtapaTipo> etapasPermitidas,
        String fabricante,
        String modelo,
        String numeroSerie,
        String localizacao,
        Boolean ativo,
        String observacoes
) {}
