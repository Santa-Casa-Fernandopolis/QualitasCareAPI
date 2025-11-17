package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.security.app.AuthContext;
import com.erp.qualitascareapi.security.app.CurrentUserExtractor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Utility component that exposes the authenticated tenant scope so that
 * services can ensure multi-tenant isolation when processing requests.
 */
@Component
public class TenantScopeGuard {

    private final CurrentUserExtractor currentUserExtractor;

    public TenantScopeGuard(CurrentUserExtractor currentUserExtractor) {
        this.currentUserExtractor = currentUserExtractor;
    }

    public Long currentTenantId() {
        return currentContext().tenantId();
    }

    public AuthContext currentContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return currentUserExtractor.from(authentication);
    }

    public void checkTenantAccess(Long tenantId) {
        Long currentTenantId = currentTenantId();
        if (tenantId == null || currentTenantId == null) {
            return;
        }
        if (!Objects.equals(currentTenantId, tenantId)) {
            throw new AccessDeniedException("User cannot access tenant " + tenantId);
        }
    }

    public void checkRequestedTenant(Long tenantId) {
        if (tenantId == null) {
            return;
        }
        checkTenantAccess(tenantId);
    }
}
