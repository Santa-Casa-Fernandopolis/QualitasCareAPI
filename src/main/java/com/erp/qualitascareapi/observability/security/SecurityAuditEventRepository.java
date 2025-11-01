package com.erp.qualitascareapi.observability.security;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SecurityAuditEventRepository extends JpaRepository<SecurityAuditEvent, Long>,
        JpaSpecificationExecutor<SecurityAuditEvent> {
}
