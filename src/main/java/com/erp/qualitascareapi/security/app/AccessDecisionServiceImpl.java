package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.domains.Policy;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.repo.*;
import org.springframework.stereotype.Service;

@Service
public class AccessDecisionServiceImpl implements AccessDecisionService {

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

        // 1) Overrides (exato + NULL)
        var ov = overrideRepo.findEffective(ctx.userId(), tenantId, res, act, f);
        if (ov.isPresent()) {
            return ov.get().getEffect() == Effect.ALLOW;
        }

        // 2) Policies (prioridade asc; DENY vence, ALLOW concede)
        for (Policy p : policyRepo.findEffective(tenantId, res, act, f)) {
            if (!p.getRoles().isEmpty()) {
                boolean any = p.getRoles().stream().anyMatch(r -> ctx.roles().contains(r.getName()));
                if (!any) continue;
            }
            if (policyEvaluator.matchesAll(p, ctx, target)) {
                if (p.getEffect() == Effect.DENY) return false;
                if (p.getEffect() == Effect.ALLOW) return true;
            }
        }

        // 3) RBAC (roles → permissions) com fallback (feature=NULL)
        return rolePermRepo.existsByRolesAndScope(ctx.roles(), tenantId, res, act, f);
    }
}

