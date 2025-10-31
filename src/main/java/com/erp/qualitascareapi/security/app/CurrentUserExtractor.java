package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class CurrentUserExtractor {

    public AuthContext from(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return AuthContext.anonymous();
        }

        Object principal = auth.getPrincipal();
        Long userId = null;
        String username = auth.getName();
        Long tenantId = null;
        String department = null;
        String profession = null;
        UserStatus status = null;
        IdentityOrigin origin = null;
        Map<String, String> attributes = new HashMap<>();

        Set<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(a -> a.replace("ROLE_", ""))
                .map(String::toUpperCase)
                .collect(Collectors.toCollection(java.util.LinkedHashSet::new));

        Jwt jwt = null;
        if (auth instanceof JwtAuthenticationToken jwtToken) {
            jwt = jwtToken.getToken();
        } else if (principal instanceof Jwt p) {
            jwt = p;
        }

        if (jwt != null) {
            userId = parseLongClaim(jwt.getClaim("user_id"));
            if (jwt.containsClaim("sub")) {
                username = jwt.getClaimAsString("sub");
            }
            tenantId = parseLongClaim(jwt.getClaim("tenant_id"));
            department = jwt.getClaimAsString("department");
            profession = jwt.getClaimAsString("profession");
            status = parseStatus(jwt.getClaimAsString("user_status"));
            origin = parseOrigin(jwt.getClaimAsString("origin"));

            Map<String, Object> claimAttributes = resolveAttributes(jwt);
            claimAttributes.forEach((k, v) -> {
                if (v != null) {
                    attributes.put(k, String.valueOf(v));
                }
            });
        } else if (principal instanceof UserDetails details) {
            username = details.getUsername();
            attributes.put("principalClass", principal.getClass().getSimpleName());
        }

        if (status == null) {
            status = UserStatus.ACTIVE;
        }
        if (origin == null) {
            origin = IdentityOrigin.LOCAL;
        }
        if (tenantId == null) {
            tenantId = extractTenantFromAuthorities(roles);
        }

        return new AuthContext(userId, username, tenantId, roles, department, profession, status, origin, attributes);
    }

    private Long parseLongClaim(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Map<String, Object> resolveAttributes(Jwt jwt) {
        if (jwt == null) {
            return Map.of();
        }

        Object direct = jwt.getClaim("attributes");
        if (direct instanceof Map<?, ?> map) {
            Map<String, Object> converted = new HashMap<>();
            map.forEach((k, v) -> {
                if (k != null) {
                    converted.put(String.valueOf(k), v);
                }
            });
            return converted;
        }

        Object clinical = jwt.getClaim("clinical_attributes");
        if (clinical instanceof Map<?, ?> map) {
            Map<String, Object> converted = new HashMap<>();
            map.forEach((k, v) -> {
                if (k != null) {
                    converted.put(String.valueOf(k), v);
                }
            });
            return converted;
        }

        if (clinical instanceof Iterable<?> iterable) {
            Map<String, Object> converted = new HashMap<>();
            int index = 0;
            for (Object o : iterable) {
                converted.put("attr_" + index++, o);
            }
            return converted;
        }

        return Map.of();
    }

    private UserStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return UserStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private IdentityOrigin parseOrigin(String origin) {
        if (origin == null || origin.isBlank()) {
            return null;
        }
        try {
            return IdentityOrigin.valueOf(origin.toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private Long extractTenantFromAuthorities(Set<String> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .filter(r -> r.startsWith("TENANT_"))
                .map(r -> r.substring("TENANT_".length()))
                .map(this::parseLongClaim)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}

