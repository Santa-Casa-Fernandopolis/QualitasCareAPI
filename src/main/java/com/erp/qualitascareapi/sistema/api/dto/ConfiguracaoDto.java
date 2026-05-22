package com.erp.qualitascareapi.sistema.api.dto;

import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import com.erp.qualitascareapi.sistema.enums.TipoValorConfiguracao;

import java.time.LocalDateTime;

/**
 * Representação pública de uma configuração de sistema.
 * Valores do tipo {@code SECRET} são retornados mascarados como {@code "****"}.
 */
public record ConfiguracaoDto(
        Long id,
        Long tenantId,
        ModuloConfiguracao modulo,
        String chave,
        String valor,
        TipoValorConfiguracao tipoValor,
        String descricao,
        boolean editavel,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
