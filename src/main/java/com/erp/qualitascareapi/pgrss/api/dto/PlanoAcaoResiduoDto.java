package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.StatusPlanoAcao;

import java.time.LocalDate;

public record PlanoAcaoResiduoDto(
        Long id,
        Long tenantId,
        Long naoConformidadeId,
        String descricaoAcao,
        String responsavelNome,
        LocalDate dataPrazo,
        LocalDate dataConclusao,
        StatusPlanoAcao status,
        String descricaoEvidencia
) {}
