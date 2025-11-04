package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

/**
 * Extrator defensivo de claims de um Jwt usando somente a API pública do Spring Security.
 * Não depende de classes internas (package-private) nem de jose.*.
 */
public class JwtPayloadExtractor {

    public Payload extract(Jwt jwt) {
        if (jwt == null) {
            return Payload.empty();
        }

        Long userId = parseLong(jwt.getClaim("user_id"));
        String username = normalize(orNull(jwt.getSubject())); // "sub" padrão do JWT
        if (username == null) {
            // fallback defensivo: tenta pegar "sub" via getClaimAsString (se vier com nome diferente)
            username = normalize(claimAsString(jwt, "sub"));
        }

        Long tenantId   = parseLong(jwt.getClaim("tenant_id"));
        String dept     = normalize(claimAsString(jwt, "department"));
        String prof     = normalize(claimAsString(jwt, "profession"));
        UserStatus st   = parseStatus(claimAsString(jwt, "user_status"));
        IdentityOrigin o= parseOrigin(claimAsString(jwt, "origin"));

        Map<String, String> attributes = collectAttributes(jwt);

        return new Payload(userId, username, tenantId, dept, prof, st, o, attributes);
    }

    /* ----------------- helpers ----------------- */

    private Map<String, String> collectAttributes(Jwt jwt) {
        if (jwt == null) return Map.of();
        LinkedHashMap<String, String> out = new LinkedHashMap<>();

        Object direct = jwt.getClaim("attributes");
        if (direct instanceof Map<?, ?> m) {
            m.forEach((k,v) -> { if (k!=null && v!=null) out.put(String.valueOf(k), String.valueOf(v)); });
        }

        Object clinical = jwt.getClaim("clinical_attributes");
        if (clinical instanceof Map<?, ?> m) {
            m.forEach((k,v) -> { if (k!=null && v!=null) out.put(String.valueOf(k), String.valueOf(v)); });
        } else if (clinical instanceof Iterable<?> it) {
            int i=0;
            for (Object v: it) if (v!=null) out.put("attr_"+(i++), String.valueOf(v));
        }

        return Collections.unmodifiableMap(out);
    }

    private String claimAsString(Jwt jwt, String name) {
        if (jwt == null) return null;
        try {
            return jwt.getClaimAsString(name);
        } catch (RuntimeException ex) {
            Object raw = jwt.getClaim(name);
            if (raw instanceof CharSequence || raw instanceof Number || raw instanceof Boolean) {
                return String.valueOf(raw);
            }
            return null;
        }
    }

    public List<String> claimAsStringList(Jwt jwt, String name) {
        if (jwt == null) return null;
        try {
            return jwt.getClaimAsStringList(name);
        } catch (RuntimeException ex) {
            Object raw = jwt.getClaim(name);
            if (raw instanceof Collection<?> c) {
                List<String> vals = new ArrayList<>();
                for (Object v: c) if (v!=null) vals.add(String.valueOf(v));
                return vals.isEmpty() ? List.of() : List.copyOf(vals);
            }
            if (raw instanceof Object[] arr) {
                List<String> vals = new ArrayList<>(arr.length);
                for (Object v: arr) if (v!=null) vals.add(String.valueOf(v));
                return vals.isEmpty() ? List.of() : List.copyOf(vals);
            }
            if (raw != null) {
                String v = normalize(String.valueOf(raw));
                if (v != null) return List.of(v);
            }
            return null;
        }
    }

    private Long parseLong(Object v) {
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s) {
            try { return Long.parseLong(s); } catch (NumberFormatException ignore) {}
        }
        return null;
    }

    private UserStatus parseStatus(String s) {
        if (s == null || s.isBlank()) return null;
        try { return UserStatus.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return null; }
    }

    private IdentityOrigin parseOrigin(String s) {
        if (s == null || s.isBlank()) return null;
        try { return IdentityOrigin.valueOf(s.trim().toUpperCase()); }
        catch (IllegalArgumentException ex) { return null; }
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private String orNull(String s) { return s == null ? null : s; }

    /* ----------------- tipo de retorno ----------------- */

    public record Payload(
            Long userId,
            String username,
            Long tenantId,
            String department,
            String profession,
            UserStatus status,
            IdentityOrigin origin,
            Map<String,String> attributes
    ) {
        public Payload {
            attributes = attributes == null ? Map.of() : Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
        }
        public static Payload empty() {
            return new Payload(null, null, null, null, null, null, null, Map.of());
        }
    }
}
