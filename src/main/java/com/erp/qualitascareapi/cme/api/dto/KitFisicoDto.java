package com.erp.qualitascareapi.cme.api.dto;

import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;
import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;

import java.time.LocalDateTime;
import java.util.List;

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
        Boolean ativo,
        StatusAprovacaoCme statusAprovacao,
        Long aprovadoPorId,
        String aprovadoPorNome,
        LocalDateTime aprovadoEm,
        Boolean composicaoConforme,
        List<String> pendenciasComposicao
) {}
