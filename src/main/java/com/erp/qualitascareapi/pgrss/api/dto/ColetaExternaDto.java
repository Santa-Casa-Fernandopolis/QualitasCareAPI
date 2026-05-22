package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.StatusColetaExterna;
import com.erp.qualitascareapi.pgrss.enums.TipoDestinoFinal;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ColetaExternaDto(
        Long id,
        Long tenantId,
        Long empresaId,
        String empresaNome,
        Long grupoId,
        String grupoCodigo,
        LocalDate dataColeta,
        BigDecimal pesoTotalKg,
        TipoDestinoFinal destinacao,
        String numeroManifesto,
        String numeroCertificadoDestinacao,
        String placaVeiculo,
        String nomeMotorista,
        String responsavelNome,
        StatusColetaExterna status,
        String observacoes
) {}
