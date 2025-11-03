package com.erp.qualitascareapi.security.auth.jose.jws;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwsPayloadExtractorTest {

    private final JwsPayloadExtractor extractor = new JwsPayloadExtractor();

    @Test
    void extractShouldNormalizeTokenClaims() {
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("user_id", "42")
                .claim("sub", "  enf.scf  ")
                .claim("tenant_id", 7)
                .claim("department", "  UTI  ")
                .claim("profession", "nurse")
                .claim("user_status", "active")
                .claim("origin", "Local")
                .claim("attributes", Map.of("crm", "1234", "shift", "night"))
                .claim("clinical_attributes", List.of("icu", "pediatrics"))
                .build();

        JwsPayloadExtractor.Payload payload = extractor.extract(jwt);

        assertThat(payload.userId()).isEqualTo(42L);
        assertThat(payload.username()).isEqualTo("enf.scf");
        assertThat(payload.tenantId()).isEqualTo(7L);
        assertThat(payload.department()).isEqualTo("UTI");
        assertThat(payload.profession()).isEqualTo("nurse");
        assertThat(payload.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(payload.origin()).isEqualTo(IdentityOrigin.LOCAL);
        assertThat(payload.attributes())
                .containsEntry("crm", "1234")
                .containsEntry("shift", "night")
                .containsEntry("attr_0", "icu")
                .containsEntry("attr_1", "pediatrics");
    }

    @Test
    void toUserDetailsShouldReflectRolesAndStatus() {
        Instant now = Instant.now();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "RS256")
                .claim("sub", "med.scf")
                .claim("roles", List.of("ADMIN", "ROLE_OPERATOR", ""))
                .claim("tenant_id", "15")
                .claim("user_status", "suspended")
                .expiresAt(now.plusSeconds(3600))
                .build();

        UserDetails details = extractor.toUserDetails(jwt);

        assertThat(details.getUsername()).isEqualTo("med.scf");
        assertThat(details.getAuthorities())
                .extracting("authority")
                .containsExactlyInAnyOrder("ROLE_ADMIN", "ROLE_OPERATOR", "TENANT_15");
        assertThat(details.isAccountNonLocked()).isFalse();
        assertThat(details.isEnabled()).isFalse();
    }
}
