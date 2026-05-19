package com.erp.qualitascareapi.observability.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Popula o MDC com identificadores chave para correlação ponta a ponta de logs.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationFilter extends OncePerRequestFilter {

    public static final String TRACE_ID = "traceId";
    public static final String USER_ID = "userId";
    public static final String CLIENT_IP = "clientIp";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String traceId = Optional.ofNullable(request.getHeader("X-Correlation-ID"))
                .filter(s -> !s.isBlank())
                .orElse(UUID.randomUUID().toString());
        String clientIp = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .map(value -> value.split(",")[0].trim())
                .filter(s -> !s.isBlank())
                .orElse(request.getRemoteAddr());

        MDC.put(TRACE_ID, traceId);
        MDC.put(CLIENT_IP, clientIp);
        MDC.put(USER_ID, "anonymous"); // updated by RequestLoggingFilter after JWT resolution

        request.setAttribute(CLIENT_IP, clientIp);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TRACE_ID);
            MDC.remove(USER_ID);
            MDC.remove(CLIENT_IP);
        }
    }
}
