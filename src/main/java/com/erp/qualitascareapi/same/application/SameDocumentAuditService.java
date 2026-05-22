package com.erp.qualitascareapi.same.application;

import com.erp.qualitascareapi.same.api.dto.SameDocumentAccessLogDto;
import com.erp.qualitascareapi.same.domain.SameClinicalDocument;
import com.erp.qualitascareapi.same.domain.SameDocumentAccessLog;
import com.erp.qualitascareapi.same.enums.SameAccessAction;
import com.erp.qualitascareapi.same.repo.SameDocumentAccessLogRepository;
import com.erp.qualitascareapi.security.app.AuthContext;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SameDocumentAuditService {

    private final SameDocumentAccessLogRepository accessLogRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public SameDocumentAuditService(SameDocumentAccessLogRepository accessLogRepository,
                                    TenantScopeGuard tenantScopeGuard) {
        this.accessLogRepository = accessLogRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public void register(SameClinicalDocument document, SameAccessAction action, HttpServletRequest request) {
        AuthContext context = tenantScopeGuard.currentContext();
        SameDocumentAccessLog log = new SameDocumentAccessLog();
        log.setTenant(document.getTenant());
        log.setClinicalDocument(document);
        log.setPatientMaster(document.getPatientMaster());
        log.setUserId(context.userId());
        log.setUserName(context.username());
        log.setAction(action);
        log.setIpAddress(clientIp(request));
        log.setUserAgent(request != null ? request.getHeader("User-Agent") : null);
        accessLogRepository.save(log);
    }

    @Transactional(readOnly = true)
    public List<SameDocumentAccessLogDto> listByDocument(Long documentId) {
        return accessLogRepository.findAllByTenantIdAndClinicalDocumentId(
                        tenantScopeGuard.currentTenantId(), documentId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<SameDocumentAccessLogDto> listByPatient(Long patientId) {
        return accessLogRepository.findAllByTenantIdAndPatientMasterId(
                        tenantScopeGuard.currentTenantId(), patientId, Pageable.unpaged())
                .getContent()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private SameDocumentAccessLogDto toDto(SameDocumentAccessLog log) {
        return new SameDocumentAccessLogDto(
                log.getId(),
                log.getClinicalDocument().getId(),
                log.getPatientMaster().getId(),
                log.getUserId(),
                log.getUserName(),
                log.getAction(),
                log.getIpAddress(),
                log.getUserAgent(),
                log.getCreatedAt()
        );
    }

    private String clientIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
