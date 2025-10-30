package com.erp.qualitascareapi.security.app;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CurrentUserExtractor {

    public AuthContext from(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return new AuthContext(null, null, Set.of(), null);
        }

        // Adapte ao seu principal / JWT
        Object principal = auth.getPrincipal();
        Long userId = null;
        Long tenantId = null;
        String department = null;

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace("ROLE_", ""))
                .collect(Collectors.toSet());

        // TODO: popular userId/tenantId/department a partir do principal ou claims

        return new AuthContext(userId, tenantId, roles, department);
    }
}

