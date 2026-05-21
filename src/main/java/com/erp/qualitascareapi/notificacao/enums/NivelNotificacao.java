package com.erp.qualitascareapi.notificacao.enums;

public enum NivelNotificacao {
    /** Informativo — sem impacto na conformidade. */
    INFO,
    /** Temperatura ou parâmetro próximo ao limite — atenção necessária. */
    ALERTA,
    /** Temperatura ou parâmetro fora da faixa — ação imediata necessária. */
    CRITICO
}
