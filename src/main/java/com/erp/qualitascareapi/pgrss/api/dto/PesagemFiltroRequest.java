package com.erp.qualitascareapi.pgrss.api.dto;

import com.erp.qualitascareapi.pgrss.enums.StatusPesagem;
import com.erp.qualitascareapi.pgrss.enums.TurnoColeta;

import java.time.LocalDate;

public record PesagemFiltroRequest(
        Long setorId,
        Long grupoId,
        Long tipoId,
        LocalDate dataInicio,
        LocalDate dataFim,
        TurnoColeta turno,
        StatusPesagem status
) {}
