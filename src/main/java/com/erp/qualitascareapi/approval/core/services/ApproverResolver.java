package com.erp.qualitascareapi.approval.core.services;

import com.erp.qualitascareapi.approval.core.enums.OrgRoleType;
import com.erp.qualitascareapi.core.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public interface ApproverResolver {
    List<User> resolveCandidates(Tenant tenant, OrgRoleType role, Setor setor);
    boolean isUserEligible(User user, OrgRoleType role, Setor setor, LocalDateTime when);
}
