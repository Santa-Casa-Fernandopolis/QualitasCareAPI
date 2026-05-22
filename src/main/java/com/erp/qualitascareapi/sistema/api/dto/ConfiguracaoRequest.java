package com.erp.qualitascareapi.sistema.api.dto;

import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import com.erp.qualitascareapi.sistema.enums.TipoValorConfiguracao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload para criação de uma nova configuração de sistema.
 * Omitir {@code tenantId} cria uma configuração global (vale para todos os tenants).
 */
public record ConfiguracaoRequest(

        /** Null = configuração global. */
        Long tenantId,

        @NotNull
        ModuloConfiguracao modulo,

        @NotBlank
        @Size(max = 100)
        String chave,

        /** Valor inicial — pode ser vazio para parâmetros a serem preenchidos depois. */
        String valor,

        @NotNull
        TipoValorConfiguracao tipoValor,

        @Size(max = 255)
        String descricao,

        boolean editavel
) {}
