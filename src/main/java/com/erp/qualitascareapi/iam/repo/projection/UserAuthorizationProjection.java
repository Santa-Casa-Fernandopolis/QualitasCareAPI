package com.erp.qualitascareapi.iam.repo.projection;

import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;

public interface UserAuthorizationProjection {
    Long getId();
    Long getTenantId();
    String getDepartment();
    UserStatus getStatus();
    IdentityOrigin getOrigin();
    String getUsername();
}
