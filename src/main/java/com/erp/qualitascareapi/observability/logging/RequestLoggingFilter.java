package com.erp.qualitascareapi.observability.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;

/**
 * Persiste um resumo do access log HTTP para consultas via API.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Set<String> SKIPPED_PATH_PREFIXES = Set.of("/actuator", "/error");

    private final RequestLogRepository repository;
    private final TransactionTemplate transactionTemplate;

    public RequestLoggingFilter(RequestLogRepository repository, PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        try {
            filterChain.doFilter(request, response);
        } finally {
            if (shouldSkip(request)) {
                return;
            }
            long durationMs = System.currentTimeMillis() - start;
            Instant timestamp = Instant.now();
            String method = request.getMethod();
            String path = request.getRequestURI();
            int status = response.getStatus();
            String traceId = MDC.get(CorrelationFilter.TRACE_ID);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userId = authentication != null ? authentication.getName() : "anonymous";
            String clientIp = (String) request.getAttribute(CorrelationFilter.CLIENT_IP);
            if (clientIp == null) {
                clientIp = request.getRemoteAddr();
            }
            String httpVersion = request.getProtocol();
            Long contentLength = null;
            String contentLengthHeader = response.getHeader("Content-Length");
            if (contentLengthHeader != null) {
                try {
                    contentLength = Long.parseLong(contentLengthHeader);
                } catch (NumberFormatException ignored) {
                }
            }

            RequestLog log = new RequestLog(timestamp, method, path, status, durationMs, traceId, userId, clientIp,
                    httpVersion, contentLength);
            transactionTemplate.executeWithoutResult(status -> repository.save(log));
        }
    }

    private boolean shouldSkip(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return SKIPPED_PATH_PREFIXES.stream().anyMatch(uri::startsWith);
    }
}
