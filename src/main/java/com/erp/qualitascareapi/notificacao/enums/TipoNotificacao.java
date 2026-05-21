package com.erp.qualitascareapi.notificacao.enums;

public enum TipoNotificacao {
    TEMPERATURA_GELADEIRA_ALERTA,
    TEMPERATURA_GELADEIRA_NAO_CONFORME,
    MONITORAMENTO_AMBIENTAL_ALERTA,
    MONITORAMENTO_AMBIENTAL_NAO_CONFORME,
    DISPOSITIVO_IOT_OFFLINE,
    /** Usuário foi designado para dar parecer em uma etapa do fluxo de aprovação de documento. */
    GED_PARECER_SOLICITADO,
    /** Usuário foi solicitado a assinar uma versão de documento. */
    GED_ASSINATURA_SOLICITADA,
    /** Licença ambiental de empresa coletora venceu. */
    PGRSS_LICENCA_VENCIDA,
    /** Licença ambiental de empresa coletora próxima do vencimento. */
    PGRSS_LICENCA_PROXIMA_VENCIMENTO,
    /** Não conformidade crítica registrada no PGRSS. */
    PGRSS_NAO_CONFORMIDADE_CRITICA,
    /** Plano de ação do PGRSS com prazo vencido. */
    PGRSS_PLANO_ACAO_VENCIDO
}
