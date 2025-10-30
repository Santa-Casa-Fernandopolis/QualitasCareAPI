package com.erp.qualitascareapi.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@Configuration
@EnableMethodSecurity // habilita @PreAuthorize com nosso PermissionEvaluator
public class MethodSecurityConfig {
    // Se o PermissionEvaluator está no contexto, o Spring irá utilizá-lo.
}

