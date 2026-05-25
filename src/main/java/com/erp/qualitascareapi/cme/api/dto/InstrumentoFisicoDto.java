package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;

public record InstrumentoFisicoDto(
        Long id,
        Long tenantId,
        Long instrumentoId,
        String instrumentoNome,
        String identificadorUnico,
        IdentificacaoFisicaStatus status,
        String localizacao,
        String observacoes,
        Boolean ativo,
        Long kitFisicoAtualId,
        String kitFisicoAtualIdentificador,
        String kitFisicoAtualModelo,
        Integer kitFisicoAtualVersaoNumero
) {}
