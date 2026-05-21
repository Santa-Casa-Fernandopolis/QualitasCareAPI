package com.erp.qualitascareapi.approval.core.repo;

import com.erp.qualitascareapi.approval.core.domain.ApprovalFlowDef;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalFlowDefRepository extends JpaRepository<ApprovalFlowDef, Long> {
    Optional<ApprovalFlowDef> findFirstByTenant_IdAndDomainAndActiveTrueOrderByIdDesc(Long tenantId, ApprovalDomain domain);
}
