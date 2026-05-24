package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.cme.enums.BowieDickStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record TesteBowieDickDto(Long id, Long autoclaveId, LocalDate dataExecucao,
                                ResultadoConformidade resultado, Long executadoPorId,
                                String executadoPorNome, Long validadorId, String validadorNome,
                                BowieDickStatus status, LocalDateTime validadoEm,
                                String parecerValidacao, String observacoes, Set<Long> evidenciasIds) {
}
