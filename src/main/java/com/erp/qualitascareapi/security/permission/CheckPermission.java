package com.erp.qualitascareapi.security.permission;

import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;

public interface CheckPermission {
    void check(ResourceType resource, Action action, String feature);
}
