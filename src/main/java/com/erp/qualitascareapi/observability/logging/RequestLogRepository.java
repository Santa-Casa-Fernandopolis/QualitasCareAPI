package com.erp.qualitascareapi.observability.logging;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RequestLogRepository extends JpaRepository<RequestLog, Long>,
        JpaSpecificationExecutor<RequestLog> {
}
