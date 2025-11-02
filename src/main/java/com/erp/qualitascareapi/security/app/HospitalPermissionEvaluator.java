package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class HospitalPermissionEvaluator implements PermissionEvaluator {

    private final AccessDecisionService accessDecisionService;
    private final CurrentUserExtractor currentUserExtractor;
    private final TargetLoader targetLoader;

    public HospitalPermissionEvaluator(AccessDecisionService accessDecisionService,
                                       CurrentUserExtractor currentUserExtractor,
                                       TargetLoader targetLoader) {
        this.accessDecisionService = accessDecisionService;
        this.currentUserExtractor = currentUserExtractor;
        this.targetLoader = targetLoader;
    }

    private static final class Parsed {
        final ResourceType res;
        final Action act;
        final String feature;
        Parsed(ResourceType r, Action a, String f) { this.res = r; this.act = a; this.feature = f; }
    }

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        AuthContext ctx = currentUserExtractor.from(auth);
        Parsed p = parse(String.valueOf(permission));
        return accessDecisionService.isAllowed(ctx, p.res, p.act, p.feature, targetDomainObject);
    }

    @Override
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        AuthContext ctx = currentUserExtractor.from(auth);
        Parsed p = parse(String.valueOf(permission));
        Object target = targetLoader.load(targetType, targetId, ctx);
        return accessDecisionService.isAllowed(ctx, p.res, p.act, p.feature, target);
    }

    private Parsed parse(String s) {
        if (s == null || s.isBlank()) throw new AccessDeniedException("Permissão ausente.");
        String[] featSplit = s.split("@", 2);
        String ra = featSplit[0].trim();
        String feature = (featSplit.length > 1 && !featSplit[1].isBlank()) ? featSplit[1].trim() : null;

        String[] raSplit = ra.split(":", 2);
        if (raSplit.length != 2) throw new AccessDeniedException("Formato inválido. Use MODULO:ACAO@FEATURE.");

        try {
            ResourceType res = ResourceType.valueOf(raSplit[0].trim().toUpperCase());
            Action act = Action.valueOf(raSplit[1].trim().toUpperCase());
            return new Parsed(res, act, feature);
        } catch (IllegalArgumentException ex) {
            throw new AccessDeniedException("Módulo/ação desconhecidos.");
        }
    }
}
