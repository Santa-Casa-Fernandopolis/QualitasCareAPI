package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.ged.enums.ConfidentialityLevel;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.ged.enums.DocumentType;

import java.time.LocalDate;

public record DocumentDto(
        Long id,
        Long tenantId,
        String tenantName,
        String codigo,
        String titulo,
        DocumentType tipo,
        DocumentStatus status,
        ConfidentialityLevel confidencialidade,
        Long setorResponsavelId,
        String setorResponsavelNome,
        LocalDate dataVigenciaInicio,
        LocalDate dataVigenciaFim,
        Long versaoAtualId,
        String versaoAtual,
        Boolean exigeTreinamento,
        Boolean necessitaParecerJuridico,
        Integer periodicidadeRevisaoMeses,
        String nivelONATarget,
        String regulacoes,
        String observacoesFluxo
) {
}
