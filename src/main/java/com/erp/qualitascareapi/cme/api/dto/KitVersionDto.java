package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record KitVersionDto(Long id, Long kitId, Integer numeroVersao, LocalDate vigenciaInicio,
                            Integer validadeDias, Boolean ativo, String observacoes,
                            StatusAprovacaoCme statusAprovacao, Long aprovadoPorId,
                            String aprovadoPorNome, LocalDateTime aprovadoEm, LocalDateTime revalidadoEm,
                            Boolean valido) {
}
