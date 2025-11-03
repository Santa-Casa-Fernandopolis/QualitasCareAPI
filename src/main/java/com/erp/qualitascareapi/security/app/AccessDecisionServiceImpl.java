package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.domain.Policy;
import com.erp.qualitascareapi.security.domain.UserPermissionOverride;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.repo.PolicyRepository;
import com.erp.qualitascareapi.security.repo.RolePermissionRepository;
import com.erp.qualitascareapi.security.repo.UserPermissionOverrideRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccessDecisionServiceImpl implements AccessDecisionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessDecisionServiceImpl.class);

    private final UserPermissionOverrideRepository overrideRepo;
    private final RolePermissionRepository rolePermRepo;
    private final PolicyRepository policyRepo;
    private final PolicyEvaluator policyEvaluator;

    public AccessDecisionServiceImpl(UserPermissionOverrideRepository overrideRepo,
                                     RolePermissionRepository rolePermRepo,
                                     PolicyRepository policyRepo,
                                     PolicyEvaluator policyEvaluator) {
        this.overrideRepo = overrideRepo;
        this.rolePermRepo = rolePermRepo;
        this.policyRepo = policyRepo;
        this.policyEvaluator = policyEvaluator;
    }

    @Override
    public boolean isAllowed(AuthContext ctx, ResourceType res, Action act, String feature, Object target) {
        Long tenantId = ctx.tenantId();
        String f = (feature == null || feature.isBlank()) ? null : feature;

        if (!ctx.isActiveUser()) {
            audit(ctx, res, act, f, target, "USER_STATUS", Effect.DENY, "status=" + ctx.status());
            return false;
        }

        if (ctx.hasRole("SYSTEM_ADMIN")) {
            audit(ctx, res, act, f, target, "SYSTEM_ADMIN_PRIVILEGE", Effect.ALLOW, "role=SYSTEM_ADMIN");
            return true;
        }

        if (tenantId == null) {
            audit(ctx, res, act, f, target, "TENANT_GUARD", Effect.DENY, "tenant_missing");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        // 1) Overrides (exato + NULL)
        var ov = overrideRepo.findEffective(ctx.userId(), tenantId, res, act, f, now, PageRequest.of(0, 1))
                .stream()
                .findFirst();
        if (ov.isPresent()) {
            UserPermissionOverride override = ov.get();
            boolean allowed = override.getEffect() == Effect.ALLOW;
            audit(ctx, res, act, f, target, "OVERRIDE", override.getEffect(),
                    "overrideId=" + override.getId());
            return allowed;
        }

        // 2) Policies (prioridade asc; DENY vence, ALLOW concede)
        boolean evaluatedPolicy = false;
        for (Policy p : policyRepo.findEffective(tenantId, res, act, f)) {
            if (!p.getRoles().isEmpty()) {
                boolean any = p.getRoles().stream().anyMatch(r -> ctx.hasRole(r.getName()));
                if (!any) continue;
            }
            evaluatedPolicy = true;
            if (policyEvaluator.matchesAll(p, ctx, target)) {
                audit(ctx, res, act, f, target, "POLICY", p.getEffect(), "policyId=" + p.getId());
                if (p.getEffect() == Effect.DENY) return false;
                if (p.getEffect() == Effect.ALLOW) return true;
            }
        }

        if (evaluatedPolicy) {
            audit(ctx, res, act, f, target, "POLICY", Effect.DENY, "no_policy_matched");
            return false;
        }

        // 3) RBAC (roles → permissions) com fallback (feature=NULL)
        Set<String> normalizedRoles = ctx.roles().stream()
                .filter(Objects::nonNull)
                .map(r -> r.trim().toUpperCase(Locale.ROOT))
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        boolean allowed = !normalizedRoles.isEmpty() &&
                rolePermRepo.existsByRolesAndScope(normalizedRoles, tenantId, res, act, f);
        audit(ctx, res, act, f, target, "RBAC", allowed ? Effect.ALLOW : Effect.DENY, "roles=" + ctx.roles());
        return allowed;
    }

    private void audit(AuthContext ctx, ResourceType res, Action act, String feature, Object target, String stage,
                       Effect effect, String details) {
        if (!LOGGER.isInfoEnabled()) {
            return;
        }
        LOGGER.info("decision stage={} effect={} userId={} tenant={} resource={} action={} feature={} origin={} details={} target={}",
                stage,
                effect,
                ctx.userId(),
                ctx.tenantId(),
                res,
                act,
                feature,
                ctx.origin(),
                details,
                target == null ? "-" : target.getClass().getSimpleName());
    }
}

