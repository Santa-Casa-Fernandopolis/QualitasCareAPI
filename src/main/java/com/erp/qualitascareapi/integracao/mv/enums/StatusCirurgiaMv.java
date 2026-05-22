package com.erp.qualitascareapi.integracao.mv.enums;

/**
 * Status de uma cirurgia conforme retornado pelo Soul MV, normalizado para
 * valores canônicos usados internamente no QualitasCare.
 */
public enum StatusCirurgiaMv {
    AGENDADA,
    EM_ANDAMENTO,
    REALIZADA,
    CANCELADA,
    SUSPENSA,
    DESCONHECIDO
}
