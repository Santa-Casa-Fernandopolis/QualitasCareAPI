package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record TesteBowieDickRequest(@NotNull Long autoclaveId,
                                    @NotNull LocalDate dataExecucao,
                                    @NotNull ResultadoConformidade resultado,
                                    Long executadoPorId,
                                    String observacoes,
                                    Set<Long> evidenciasIds) {
}
