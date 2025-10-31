package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record AuthContext(Long userId,
                          String username,
                          Long tenantId,
                          Set<String> roles,
                          String department,
                          String profession,
                          UserStatus status,
                          IdentityOrigin origin,
                          Map<String, String> attributes) {

    public AuthContext {
        roles = roles == null ? Set.of() : Collections.unmodifiableSet(roles);
        attributes = attributes == null ? Map.of() : Collections.unmodifiableMap(attributes);
    }

    public static AuthContext anonymous() {
        return new AuthContext(null, null, null, Set.of(), null, null, null, null, Map.of());
    }

    public boolean isActiveUser() {
        return status == null || status.isActive();
    }

    public String attribute(String key) {
        if (key == null) {
            return null;
        }
        return attributes.get(key);
    }

    public String tenantIdAsString() {
        return tenantId == null ? null : String.valueOf(tenantId);
    }

    public boolean hasRole(String roleName) {
        if (roleName == null) {
            return false;
        }
        return roles.stream().anyMatch(r -> Objects.equals(r, roleName) || Objects.equals(r, roleName.toUpperCase()));
    }
}
