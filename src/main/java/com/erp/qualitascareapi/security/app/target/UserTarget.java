package com.erp.qualitascareapi.security.app.target;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;

/**
 * Lightweight representation of a user used during authorization decisions.
 */
public final class UserTarget {

    private final Long id;
    private final Long tenantId;
    private final String department;
    private final UserStatus status;
    private final IdentityOrigin origin;
    private final String username;

    public UserTarget(Long id,
                      Long tenantId,
                      String department,
                      UserStatus status,
                      IdentityOrigin origin,
                      String username) {
        this.id = id;
        this.tenantId = tenantId;
        this.department = department;
        this.status = status;
        this.origin = origin;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public String getDepartment() {
        return department;
    }

    public UserStatus getStatus() {
        return status;
    }

    public IdentityOrigin getOrigin() {
        return origin;
    }

    public String getOwnerId() {
        return username;
    }

    public String getUsername() {
        return username;
    }
}
