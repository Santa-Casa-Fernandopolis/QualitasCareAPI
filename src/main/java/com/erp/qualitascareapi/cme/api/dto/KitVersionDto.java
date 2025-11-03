package com.erp.qualitascareapi.cme.api.dto;

import java.time.LocalDate;

public record KitVersionDto(Long id, Long kitId, Integer numeroVersao, LocalDate vigenciaInicio,
                            Integer validadeDias, Boolean ativo, String observacoes) {
}
