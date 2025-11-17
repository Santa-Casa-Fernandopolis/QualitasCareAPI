package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.security.api.dto.CurrentUserPermissionsResponse;
import com.erp.qualitascareapi.security.app.AuthContext;
import com.erp.qualitascareapi.security.app.CurrentUserExtractor;
import com.erp.qualitascareapi.security.repo.RolePermissionRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CurrentUserPermissionsService {

    private final RolePermissionRepository rolePermissionRepository;
    private final CurrentUserExtractor currentUserExtractor;

    public CurrentUserPermissionsService(RolePermissionRepository rolePermissionRepository,
                                         CurrentUserExtractor currentUserExtractor) {
        this.rolePermissionRepository = rolePermissionRepository;
        this.currentUserExtractor = currentUserExtractor;
    }

    public CurrentUserPermissionsResponse getPermissions(Authentication authentication) {
        AuthContext context = currentUserExtractor.from(authentication);
        if (context.userId() == null) {
            throw new AccessDeniedException("Usuário não autenticado");
        }
        if (context.tenantId() == null) {
            throw new AccessDeniedException("Tenant não informado para o usuário atual");
        }

        Set<String> roles = extractBusinessRoles(context);
        Set<String> roleSnapshot = Collections.unmodifiableSet(new LinkedHashSet<>(roles));
        List<String> permissions = roles.isEmpty()
                ? List.of()
                : rolePermissionRepository.findPermissionCodesByRolesAndTenant(roles, context.tenantId())
                .stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(code -> !code.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        return new CurrentUserPermissionsResponse(
                context.userId(),
                context.username(),
                context.tenantId(),
                context.department(),
                roleSnapshot,
                permissions
        );
    }

    private Set<String> extractBusinessRoles(AuthContext context) {
        if (context.roles() == null || context.roles().isEmpty()) {
            return Collections.emptySet();
        }
        return context.roles().stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(role -> !role.isBlank())
                .map(String::toUpperCase)
                .filter(role -> !role.startsWith("TENANT_"))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
