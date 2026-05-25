package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;

public record KitFisicoDto(
        Long id,
        Long tenantId,
        Long kitId,
        String kitNome,
        Long kitVersaoAtualId,
        Integer kitVersaoAtualNumero,
        String identificadorUnico,
        IdentificacaoFisicaStatus status,
        String localizacao,
        String observacoes,
        Boolean ativo
) {}
