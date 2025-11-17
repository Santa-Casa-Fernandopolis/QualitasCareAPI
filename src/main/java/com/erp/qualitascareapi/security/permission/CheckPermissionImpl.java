package com.erp.qualitascareapi.security.permission;

import com.erp.qualitascareapi.security.app.AccessDecisionService;
import com.erp.qualitascareapi.security.app.AuthContext;
import com.erp.qualitascareapi.security.app.CurrentUserExtractor;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CheckPermissionImpl implements CheckPermission {

    private final AccessDecisionService accessDecisionService;
    private final CurrentUserExtractor currentUserExtractor;

    public CheckPermissionImpl(AccessDecisionService accessDecisionService,
                               CurrentUserExtractor currentUserExtractor) {
        this.accessDecisionService = accessDecisionService;
        this.currentUserExtractor = currentUserExtractor;
    }

    @Override
    public void check(ResourceType resource, Action action, String feature) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthContext context = currentUserExtractor.from(authentication);
        String normalizedFeature = feature == null || feature.isBlank() ? null : feature;
        boolean allowed = accessDecisionService.isAllowed(context, resource, action, normalizedFeature, null);
        if (!allowed) {
            throw new AccessDeniedException("Access denied for resource " + resource + " and action " + action);
        }
    }
}
