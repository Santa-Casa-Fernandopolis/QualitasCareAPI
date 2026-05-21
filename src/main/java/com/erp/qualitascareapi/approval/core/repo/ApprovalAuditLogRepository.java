package com.erp.qualitascareapi.approval.core.repo;

import com.erp.qualitascareapi.approval.core.domain.ApprovalAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalAuditLogRepository extends JpaRepository<ApprovalAuditLog, Long> {
}
