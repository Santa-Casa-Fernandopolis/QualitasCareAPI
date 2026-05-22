package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.StatusPesagem;
import com.erp.qualitascareapi.pgrss.enums.TurnoColeta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PesagemResiduoDto(
        Long id,
        Long tenantId,
        Long setorId,
        String setorNome,
        Long tipoId,
        String tipoNome,
        Long grupoId,
        String grupoCodigo,
        LocalDateTime dataHoraPesagem,
        BigDecimal pesoKg,
        TurnoColeta turno,
        String rota,
        String responsavelNome,
        String identificacaoBalanca,
        StatusPesagem status,
        String observacoes
) {}
