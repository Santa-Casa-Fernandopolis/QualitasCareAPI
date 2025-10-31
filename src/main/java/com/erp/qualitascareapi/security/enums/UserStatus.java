package com.erp.qualitascareapi.security.enums;

/**
 * Estados possíveis para o ciclo de vida de um usuário.
 */
public enum UserStatus {
    PROVISIONED(false),
    ACTIVE(true),
    SUSPENDED(false),
    DISABLED(false),
    EXPIRED(false);

    private final boolean active;

    UserStatus(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }
}
