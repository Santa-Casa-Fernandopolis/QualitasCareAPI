package com.erp.qualitascareapi.integracao.mv.api.dto;

import java.time.LocalDateTime;

/**
 * Resultado de uma execução do job de sincronização MV.
 * Retornado pelo endpoint de sync manual e registrado em log.
 */
public record SincronizacaoResultadoDto(
        LocalDateTime executadoEm,
        String estrategiaUsada,
        int totalEncontradas,
        int inseridas,
        int atualizadas,

        /** {@code SUCESSO}, {@code FALHA} ou {@code DESATIVADA}. */
        String status,

        /** Preenchido apenas quando {@code status = FALHA}. */
        String mensagemErro
) {

    public static SincronizacaoResultadoDto sucesso(LocalDateTime executadoEm, String estrategia,
                                                    int total, int inseridas, int atualizadas) {
        return new SincronizacaoResultadoDto(executadoEm, estrategia, total, inseridas, atualizadas,
                "SUCESSO", null);
    }

    public static SincronizacaoResultadoDto falha(LocalDateTime executadoEm, String estrategia,
                                                  String mensagem) {
        return new SincronizacaoResultadoDto(executadoEm, estrategia, 0, 0, 0,
                "FALHA", mensagem);
    }

    public static SincronizacaoResultadoDto desativada(LocalDateTime executadoEm) {
        return new SincronizacaoResultadoDto(executadoEm, "N/A", 0, 0, 0,
                "DESATIVADA", "Integração desativada — configure MV_INTEGRACAO_ATIVA=true");
    }
}
