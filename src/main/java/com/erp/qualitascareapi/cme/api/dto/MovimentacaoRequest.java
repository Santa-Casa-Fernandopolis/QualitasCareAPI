package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.MovimentacaoTipo;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record MovimentacaoRequest(@NotNull Long tenantId,
                                  Long loteId,
                                  Long setorOrigemId,
                                  Long setorDestinoId,
                                  @NotNull MovimentacaoTipo tipo,
                                  @NotNull LocalDateTime dataHora,
                                  Long responsavelId,
                                  String observacoes) {
}
