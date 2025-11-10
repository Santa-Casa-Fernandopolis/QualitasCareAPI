package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.quality.enums.NaoConformidadeSeveridade;
import com.erp.qualitascareapi.quality.enums.NaoConformidadeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.Set;

public record NaoConformidadeRequest(@NotNull Long tenantId,
                                     @NotBlank String titulo,
                                     String descricao,
                                     @NotNull NaoConformidadeSeveridade severidade,
                                     @NotNull Long tipoId,
                                     NaoConformidadeStatus status,
                                     @NotNull LocalDate dataAbertura,
                                     LocalDate dataEncerramento,
                                     Long responsavelId,
                                     String planoAcaoResumo,
                                     Set<Long> evidenciasIds) {
}
