package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ManutencaoStatus;
import com.erp.qualitascareapi.cme.enums.ManutencaoTipo;

import java.time.LocalDate;
import java.util.Set;

public record ManutencaoDto(Long id, Long autoclaveId, ManutencaoTipo tipo, ManutencaoStatus status,
                            LocalDate dataAgendamento, LocalDate dataExecucao, String responsavelTecnico,
                            String observacoes, Set<Long> evidenciasIds) {
}
