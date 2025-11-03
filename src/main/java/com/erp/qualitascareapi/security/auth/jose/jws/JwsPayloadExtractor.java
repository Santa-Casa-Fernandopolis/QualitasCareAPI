package com.erp.qualitascareapi.security.auth.jose.jws;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Utility responsible for converting JWT/JWS payloads produced by the authorization
 * server into strongly typed values consumed by the application. The extractor
 * is intentionally defensive because tokens may miss optional claims or present
 * them in unexpected formats depending on the identity provider that issued the
 * token.
 */
public class JwsPayloadExtractor {

    /**
     * Extracts a snapshot of the known user related claims from the provided JWT.
     * Missing or malformed values are ignored to avoid breaking the authentication
     * flow when a downstream provider omits optional information.
     *
     * @param jwt the token received from Spring Security's resource server support.
     * @return a {@link Payload} instance with the normalized values.
     */
    public Payload extract(Jwt jwt) {
        if (jwt == null) {
            return Payload.empty();
        }

        Long userId = parseLong(jwt.getClaim("user_id"));
        String username = normalize(jwt.containsClaim("sub") ? jwt.getClaimAsString("sub") : null);
        Long tenantId = parseLong(jwt.getClaim("tenant_id"));
        String department = normalize(jwt.getClaimAsString("department"));
        String profession = normalize(jwt.getClaimAsString("profession"));
        UserStatus status = parseStatus(jwt.getClaimAsString("user_status"));
        IdentityOrigin origin = parseOrigin(jwt.getClaimAsString("origin"));
        Map<String, String> attributes = collectAttributes(jwt);

        return new Payload(userId, username, tenantId, department, profession, status, origin, attributes);
    }

    /**
     * Builds a lightweight {@link UserDetails} instance backed by the claims of the
     * given JWT. The result is useful when Spring Security needs to expose a
     * {@link UserDetails} principal even though no local user entity was resolved.
     *
     * @param jwt the token to convert.
     * @return a minimal {@link UserDetails} implementation reflecting the token contents.
     */
    public UserDetails toUserDetails(Jwt jwt) {
        Payload payload = extract(jwt);
        String username = payload.username() != null ? payload.username() : Optional.ofNullable(jwt.getSubject()).orElse("anonymous");
        Collection<? extends GrantedAuthority> authorities = resolveAuthorities(jwt, payload.tenantId());
        return new MinimalJwtUserDetails(username, payload.status(), authorities, jwt.getExpiresAt());
    }

    private Collection<? extends GrantedAuthority> resolveAuthorities(Jwt jwt, Long tenantId) {
        List<String> roles = jwt != null ? jwt.getClaimAsStringList("roles") : null;
        LinkedHashSet<GrantedAuthority> authorities = new LinkedHashSet<>();
        if (roles != null) {
            for (String role : roles) {
                String normalized = normalize(role);
                if (normalized == null || normalized.isBlank()) {
                    continue;
                }
                String authority = normalized.startsWith("ROLE_") ? normalized : "ROLE_" + normalized;
                authorities.add(new SimpleGrantedAuthority(authority.toUpperCase()));
            }
        }
        if (tenantId != null) {
            authorities.add(new SimpleGrantedAuthority("TENANT_" + tenantId));
        }
        return List.copyOf(authorities);
    }

    private Map<String, String> collectAttributes(Jwt jwt) {
        if (jwt == null) {
            return Map.of();
        }
        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();

        Object direct = jwt.getClaim("attributes");
        if (direct instanceof Map<?, ?> directMap) {
            directMap.forEach((key, value) -> {
                if (key != null && value != null) {
                    attributes.put(String.valueOf(key), String.valueOf(value));
                }
            });
        }

        Object clinical = jwt.getClaim("clinical_attributes");
        if (clinical instanceof Map<?, ?> clinicalMap) {
            clinicalMap.forEach((key, value) -> {
                if (key != null && value != null) {
                    attributes.put(String.valueOf(key), String.valueOf(value));
                }
            });
        } else if (clinical instanceof Iterable<?> iterable) {
            int index = 0;
            for (Object value : iterable) {
                if (value != null) {
                    attributes.put("attr_" + index++, String.valueOf(value));
                }
            }
        }

        return Collections.unmodifiableMap(attributes);
    }

    private Long parseLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text) {
            try {
                return Long.parseLong(text);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
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

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static final class MinimalJwtUserDetails implements UserDetails {

        private final String username;
        private final UserStatus status;
        private final Collection<? extends GrantedAuthority> authorities;
        private final Instant expiresAt;

        private MinimalJwtUserDetails(String username,
                                      UserStatus status,
                                      Collection<? extends GrantedAuthority> authorities,
                                      Instant expiresAt) {
            this.username = Objects.requireNonNullElse(username, "anonymous");
            this.status = status;
            this.authorities = authorities == null ? List.of() : authorities;
            this.expiresAt = expiresAt;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return null;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return status != UserStatus.EXPIRED;
        }

        @Override
        public boolean isAccountNonLocked() {
            return status != UserStatus.SUSPENDED && status != UserStatus.DISABLED;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            if (expiresAt == null) {
                return true;
            }
            return expiresAt.isAfter(Instant.now());
        }

        @Override
        public boolean isEnabled() {
            return status == null || status.isActive();
        }
    }

    public record Payload(Long userId,
                          String username,
                          Long tenantId,
                          String department,
                          String profession,
                          UserStatus status,
                          IdentityOrigin origin,
                          Map<String, String> attributes) {

        public Payload {
            attributes = attributes == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        }

        static Payload empty() {
            return new Payload(null, null, null, null, null, null, null, Map.of());
        }
    }
}
