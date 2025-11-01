package com.erp.qualitascareapi.observability.logging;

import com.erp.qualitascareapi.observability.logging.dto.RequestLogResponse;
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
@RequestMapping("/api/logs/operational")
public class OperationalLogController {

    private final RequestLogQueryService service;

    public OperationalLogController(RequestLogQueryService service) {
        this.service = service;
    }

    @GetMapping
    public Page<RequestLogResponse> search(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(required = false) String method,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String traceId,
            @RequestParam(required = false) String path,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
        Page<RequestLog> requestLogs = service.search(new RequestLogFilter(from, to, method, status, userId, traceId, path), pageable);
        List<RequestLogResponse> content = requestLogs.stream()
                .map(log -> new RequestLogResponse(log.getId(), log.getTimestamp(), log.getMethod(), log.getPath(),
                        log.getStatus(), log.getDurationMs(), log.getTraceId(), log.getUserId(), log.getClientIp(),
                        log.getHttpVersion(), log.getContentLength()))
                .toList();
        return new PageImpl<>(content, pageable, requestLogs.getTotalElements());
    }
}
