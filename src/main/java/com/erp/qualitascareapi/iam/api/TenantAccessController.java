package com.erp.qualitascareapi.iam.api;

import com.erp.qualitascareapi.iam.api.dto.TenantLoginOptionDto;
import com.erp.qualitascareapi.iam.application.TenantService;
import com.erp.qualitascareapi.observability.security.SecurityAuditEvent;
import com.erp.qualitascareapi.observability.security.SecurityAuditEventRepository;
import com.erp.qualitascareapi.observability.security.SecurityAuditEventType;
import com.erp.qualitascareapi.observability.logging.CorrelationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/auth/tenants")
public class TenantAccessController {

    private final TenantService tenantService;
    private final SecurityAuditEventRepository auditRepository;

    public TenantAccessController(TenantService tenantService,
                                   SecurityAuditEventRepository auditRepository) {
        this.tenantService = tenantService;
        this.auditRepository = auditRepository;
    }

    @GetMapping
    public List<TenantLoginOptionDto> findAvailableTenants(@RequestParam("username") String username,
                                                            HttpServletRequest request) {
        List<TenantLoginOptionDto> result = tenantService.findAvailableTenantsForUsername(username);
        if (result.isEmpty() && username != null && !username.isBlank()) {
            auditRepository.save(new SecurityAuditEvent(
                    Instant.now(),
                    username.trim(),
                    SecurityAuditEventType.USER_ENUMERATION_ATTEMPT,
                    resolveClientIp(request),
                    MDC.get(CorrelationFilter.TRACE_ID),
                    "Tenant discovery sem resultado para usuário desconhecido"
            ));
        }
        return result;
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
