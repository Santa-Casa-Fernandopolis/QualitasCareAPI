package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;

import java.time.LocalDate;
import java.util.Set;

public record TesteBowieDickDto(Long id, Long autoclaveId, LocalDate dataExecucao,
                                ResultadoConformidade resultado, Long executadoPorId,
                                String observacoes, Set<Long> evidenciasIds) {
}
