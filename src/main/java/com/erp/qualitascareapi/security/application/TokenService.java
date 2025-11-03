package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.iam.application.AuthenticatedUserDetails;
import com.erp.qualitascareapi.security.api.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jose.jws.JwsHeader;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final Duration accessTokenTtl;
    private final String issuer;

    public TokenService(JwtEncoder jwtEncoder,
                        @Value("${app.security.auth.access-token-ttl:PT15M}") Duration accessTokenTtl,
                        @Value("${app.security.auth-server.issuer:http://localhost:8080}") String issuer) {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenTtl = accessTokenTtl;
        this.issuer = issuer;
    }

    public LoginResponse generateToken(Authentication authentication) {
        Objects.requireNonNull(authentication, "authentication must not be null");

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AuthenticatedUserDetails userDetails)) {
            String principalType = principal != null ? principal.getClass().getName() : "null";
            throw new IllegalArgumentException("Unsupported principal type: " + principalType);
        }

        Instant now = Instant.now();
        Instant expiresAt = now.plus(accessTokenTtl);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(authority -> !authority.isBlank())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(userDetails.getUsername())
                .claim("token_type", "access_token")
                .claim("user_id", userDetails.getId())
                .claim("tenant_id", userDetails.getTenantId())
                .claim("tenant_code", userDetails.getTenantCode())
                .claim("user_status", userDetails.getStatus() != null ? userDetails.getStatus().name() : null)
                .claim("origin", userDetails.getOrigin() != null ? userDetails.getOrigin().name() : null)
                .claim("full_name", userDetails.getFullName())
                .claim("department", userDetails.getDepartment())
                .claim("roles", roles)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(SignatureAlgorithm.RS256).type("JWT").build(),
                claims
        )).getTokenValue();

        long expiresIn = accessTokenTtl.getSeconds();
        if (expiresIn <= 0) {
            expiresIn = Duration.between(now, expiresAt).getSeconds();
        }

        return new LoginResponse(token, "Bearer", expiresIn, expiresAt);
    }
}
