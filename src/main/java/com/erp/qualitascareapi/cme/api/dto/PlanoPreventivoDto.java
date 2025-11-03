package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;

public record PlanoPreventivoDto(Long id, Long autoclaveId, Integer periodicidadeDias,
                                 Integer limiteCiclos, LocalDate proximaExecucaoPrevista, String descricao) {
}
