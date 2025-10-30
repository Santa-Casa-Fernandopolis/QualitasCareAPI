package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;

public interface AccessDecisionService {
    boolean isAllowed(AuthContext ctx, ResourceType res, Action act, String feature, Object target);
}

