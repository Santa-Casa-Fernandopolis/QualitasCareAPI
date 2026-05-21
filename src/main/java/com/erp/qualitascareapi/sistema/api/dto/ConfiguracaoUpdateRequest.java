package com.erp.qualitascareapi.sistema.api.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Payload para atualização do valor de uma configuração existente.
 * String vazia é permitida para limpar o valor de um parâmetro.
 */
public record ConfiguracaoUpdateRequest(
        @NotNull
        String valor
) {}
