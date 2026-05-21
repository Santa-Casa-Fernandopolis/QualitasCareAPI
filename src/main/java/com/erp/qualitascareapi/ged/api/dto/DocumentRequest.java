package com.erp.qualitascareapi.ged.api.dto;

import com.erp.qualitascareapi.ged.enums.ConfidentialityLevel;
import com.erp.qualitascareapi.ged.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DocumentRequest(
        @NotNull Long tenantId,
        @NotBlank String codigo,
        @NotBlank String titulo,
        @NotNull DocumentType tipo,
        @NotNull ConfidentialityLevel confidencialidade,
        Long setorResponsavelId,
        LocalDate dataVigenciaInicio,
        LocalDate dataVigenciaFim,
        Boolean exigeTreinamento,
        Boolean necessitaParecerJuridico,
        Integer periodicidadeRevisaoMeses,
        String nivelONATarget,
        String regulacoes,
        String observacoesFluxo
) {
}
