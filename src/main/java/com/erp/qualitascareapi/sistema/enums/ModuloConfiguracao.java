package com.erp.qualitascareapi.sistema.enums;

/**
 * Agrupador lógico das configurações de sistema.
 * Cada módulo da aplicação possui seu próprio namespace de chaves.
 */
public enum ModuloConfiguracao {

    /** Parâmetros gerais da aplicação. */
    SISTEMA,

    /** Integração com Soul MV (API REST ou banco de dados direto). */
    MV,

    /** Parâmetros operacionais do CME. */
    CME,

    /** Parâmetros do módulo de Monitoramento Ambiental / IoT. */
    AMBIENTAL,

    /** Parâmetros do módulo de Gestão de Documentos (GED). */
    GED,

    /** Parâmetros de IAM / segurança. */
    IAM
}
