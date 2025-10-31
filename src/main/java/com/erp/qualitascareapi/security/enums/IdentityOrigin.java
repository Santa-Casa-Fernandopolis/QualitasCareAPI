package com.erp.qualitascareapi.security.enums;

/**
 * Indica a fonte de provisionamento de uma identidade.
 * Mantemos a lista curta para permitir validações e auditoria simples.
 */
public enum IdentityOrigin {
    LOCAL,
    LDAP,
    SSO,
    IMPORTED
}
