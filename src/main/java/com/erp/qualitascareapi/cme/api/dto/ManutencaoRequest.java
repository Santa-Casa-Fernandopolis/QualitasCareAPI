package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ManutencaoStatus;
import com.erp.qualitascareapi.cme.enums.ManutencaoTipo;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record ManutencaoRequest(@NotNull Long autoclaveId,
                                @NotNull ManutencaoTipo tipo,
                                ManutencaoStatus status,
                                LocalDate dataAgendamento,
                                LocalDate dataExecucao,
                                String responsavelTecnico,
                                String observacoes,
                                Set<Long> evidenciasIds) {
}
