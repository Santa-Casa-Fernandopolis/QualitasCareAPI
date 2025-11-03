package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.MovimentacaoTipo;

import java.time.LocalDateTime;

public record MovimentacaoDto(Long id, Long tenantId, Long loteId, Long setorOrigemId, Long setorDestinoId,
                              MovimentacaoTipo tipo, LocalDateTime dataHora, Long responsavelId,
                              String observacoes) {
}
