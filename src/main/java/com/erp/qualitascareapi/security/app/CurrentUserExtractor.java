package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.auth.jose.jws.JwsPayloadExtractor;
import com.erp.qualitascareapi.security.auth.jose.jws.JwsPayloadExtractor.Payload;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CurrentUserExtractor {

    private final JwsPayloadExtractor jwsPayloadExtractor = new JwsPayloadExtractor();

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
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Jwt jwt = null;
        if (auth instanceof JwtAuthenticationToken jwtToken) {
            jwt = jwtToken.getToken();
        } else if (principal instanceof Jwt p) {
            jwt = p;
        }

        if (jwt != null) {
            Payload payload = jwsPayloadExtractor.extract(jwt);
            if (payload.userId() != null) {
                userId = payload.userId();
            }
            if (payload.username() != null) {
                username = payload.username();
            }
            if (payload.tenantId() != null) {
                tenantId = payload.tenantId();
            }
            if (payload.department() != null) {
                department = payload.department();
            }
            if (payload.profession() != null) {
                profession = payload.profession();
            }
            if (payload.status() != null) {
                status = payload.status();
            }
            if (payload.origin() != null) {
                origin = payload.origin();
            }
            attributes.putAll(payload.attributes());
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

    private Long parseLongClaim(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}

