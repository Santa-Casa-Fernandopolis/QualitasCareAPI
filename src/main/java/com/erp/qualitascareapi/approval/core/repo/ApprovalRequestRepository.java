package com.erp.qualitascareapi.approval.core.repo;

import com.erp.qualitascareapi.approval.core.domain.ApprovalRequest;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.approval.core.enums.ApprovalRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, Long> {
    Optional<ApprovalRequest> findFirstByTenant_IdAndDomainAndTargetKeyOrderByRequestedAtDesc(Long tenantId, ApprovalDomain domain, String targetKey);
    long countByTenant_IdAndDomainAndStatus(Long tenantId, ApprovalDomain domain, ApprovalRequestStatus status);
}
