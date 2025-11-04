package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.iam.application.AuthenticatedUserDetails;
import com.erp.qualitascareapi.security.api.dto.LoginResponse;
import com.nimbusds.jose.Algorithm;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JoseHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TokenService {

    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    private final JwtEncoder jwtEncoder;
    private final Duration accessTokenTtl;
    private final String issuer;
    private final JwsAlgorithm jwsAlgorithm;

    public TokenService(JwtEncoder jwtEncoder,
                        JWKSource<SecurityContext> jwkSource,
                        @Value("${app.security.auth.access-token-ttl:PT15M}") Duration accessTokenTtl,
                        @Value("${app.security.auth-server.issuer:http://localhost:8080}") String issuer) {
        this.jwtEncoder = jwtEncoder;
        this.accessTokenTtl = accessTokenTtl;
        this.issuer = issuer;
        this.jwsAlgorithm = resolveAlgorithm(jwkSource);
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

        JoseHeader joseHeader = JoseHeader.withAlgorithm(jwsAlgorithm.getName())
                .type("JWT")
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(joseHeader, claims))
                .getTokenValue();

        long expiresIn = accessTokenTtl.getSeconds();
        if (expiresIn <= 0) {
            expiresIn = Duration.between(now, expiresAt).getSeconds();
        }

        return new LoginResponse(token, "Bearer", expiresIn, expiresAt);
    }

    private JwsAlgorithm resolveAlgorithm(JWKSource<SecurityContext> jwkSource) {
        if (jwkSource == null) {
            logger.warn("JWKSource not available; defaulting token signature algorithm to RS256");
            return SignatureAlgorithm.RS256;
        }

        try {
            JWKSelector selector = new JWKSelector(new JWKMatcher.Builder().build());
            List<JWK> jwks = jwkSource.get(selector, null);
            if (jwks == null || jwks.isEmpty()) {
                logger.warn("No JWK available; defaulting token signature algorithm to RS256");
                return SignatureAlgorithm.RS256;
            }

            JWK jwk = jwks.get(0);

            Algorithm alg = jwk.getAlgorithm();
            if (alg instanceof JWSAlgorithm jwsAlg) {
                JwsAlgorithm resolved = mapAlgorithm(jwsAlg);
                if (resolved != null) {
                    return resolved;
                }
            }

            KeyType keyType = jwk.getKeyType();
            if (KeyType.RSA.equals(keyType)) {
                return SignatureAlgorithm.RS256;
            }
            if (KeyType.EC.equals(keyType)) {
                return SignatureAlgorithm.ES256;
            }
            if (KeyType.OCT.equals(keyType)) {
                return MacAlgorithm.HS256;
            }

            logger.warn("Unsupported key type '{}' for JWT signing; defaulting to RS256", keyType);
            return SignatureAlgorithm.RS256;
        } catch (Exception ex) {
            logger.warn("Failed to resolve JWT signature algorithm; defaulting to RS256", ex);
            return SignatureAlgorithm.RS256;
        }
    }

    private JwsAlgorithm mapAlgorithm(JWSAlgorithm algorithm) {
        if (JWSAlgorithm.Family.RSA.contains(algorithm)) {
            if (JWSAlgorithm.RS384.equals(algorithm)) {
                return SignatureAlgorithm.RS384;
            }
            if (JWSAlgorithm.RS512.equals(algorithm)) {
                return SignatureAlgorithm.RS512;
            }
            return SignatureAlgorithm.RS256;
        }
        if (JWSAlgorithm.Family.EC.contains(algorithm)) {
            if (JWSAlgorithm.ES384.equals(algorithm)) {
                return SignatureAlgorithm.ES384;
            }
            if (JWSAlgorithm.ES512.equals(algorithm)) {
                return SignatureAlgorithm.ES512;
            }
            return SignatureAlgorithm.ES256;
        }
        if (JWSAlgorithm.Family.HMAC_SHA.contains(algorithm)) {
            if (JWSAlgorithm.HS384.equals(algorithm)) {
                return MacAlgorithm.HS384;
            }
            if (JWSAlgorithm.HS512.equals(algorithm)) {
                return MacAlgorithm.HS512;
            }
            return MacAlgorithm.HS256;
        }
        return null;
    }
}
