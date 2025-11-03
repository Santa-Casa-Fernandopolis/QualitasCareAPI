package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.CicloStatus;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CicloEsterilizacaoRequest(@NotNull Long tenantId,
                                        @NotNull Long autoclaveId,
                                        Long loteId,
                                        @NotNull LocalDateTime inicio,
                                        LocalDateTime fim,
                                        Integer duracaoMinutos,
                                        Double temperaturaMaxima,
                                        Double pressaoMaxima,
                                        CicloStatus status,
                                        Long liberadoPorId,
                                        String observacoes) {
}
