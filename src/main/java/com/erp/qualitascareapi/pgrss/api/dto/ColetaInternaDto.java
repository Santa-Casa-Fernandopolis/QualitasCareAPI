package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.StatusColetaInterna;

import java.time.LocalDateTime;
import java.util.List;

public record ColetaInternaDto(
        Long id,
        Long tenantId,
        LocalDateTime dataHoraColeta,
        String nomeRota,
        String responsavelNome,
        StatusColetaInterna status,
        String observacoes,
        List<PesagemResiduoDto> pesagens
) {}
