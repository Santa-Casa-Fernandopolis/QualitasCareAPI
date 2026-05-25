package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import com.erp.qualitascareapi.cme.enums.CmeTipoEquipamento;

import java.util.Set;

public record CmeEquipamentoDto(
        Long id,
        Long tenantId,
        String codigo,
        String nome,
        CmeTipoEquipamento tipoEquipamento,
        Set<CmeEtapaTipo> etapasPermitidas,
        String fabricante,
        String modelo,
        String numeroSerie,
        String localizacao,
        boolean ativo,
        String observacoes
) {}
