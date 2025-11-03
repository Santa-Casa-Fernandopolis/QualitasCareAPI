package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.NaoConformidadeSeveridade;
import com.erp.qualitascareapi.cme.enums.NaoConformidadeStatus;

import java.time.LocalDate;
import java.util.Set;

public record NaoConformidadeDto(Long id, Long tenantId, String titulo, String descricao,
                                 NaoConformidadeSeveridade severidade, NaoConformidadeStatus status,
                                 LocalDate dataAbertura, LocalDate dataEncerramento, Long responsavelId,
                                 String planoAcaoResumo, Set<Long> evidenciasIds) {
}
