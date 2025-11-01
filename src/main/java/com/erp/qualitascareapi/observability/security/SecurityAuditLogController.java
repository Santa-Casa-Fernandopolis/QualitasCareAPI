package com.erp.qualitascareapi.observability.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/logs/security")
public class SecurityAuditLogController {

    private final SecurityAuditQueryService service;

    public SecurityAuditLogController(SecurityAuditQueryService service) {
        this.service = service;
    }

    @GetMapping
    public Page<SecurityAuditLogResponse> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) SecurityAuditEventType eventType,
            @RequestParam(required = false) String traceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<SecurityAuditEvent> events = service.search(new SecurityAuditEventFilter(from, to, username, eventType, traceId), pageable);
        List<SecurityAuditLogResponse> content = events.stream()
                .map(event -> new SecurityAuditLogResponse(event.getId(), event.getTimestamp(), event.getUsername(),
                        event.getEventType(), event.getClientIp(), event.getTraceId(), event.getDescription()))
                .toList();
        return new PageImpl<>(content, pageable, events.getTotalElements());
    }
}
