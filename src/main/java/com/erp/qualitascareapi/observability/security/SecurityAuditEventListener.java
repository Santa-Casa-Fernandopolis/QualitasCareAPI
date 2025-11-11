package com.erp.qualitascareapi.observability.security;

import com.erp.qualitascareapi.observability.logging.CorrelationFilter;
import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class SecurityAuditEventListener {

    private final SecurityAuditEventRepository repository;

    public SecurityAuditEventListener(SecurityAuditEventRepository repository) {
        this.repository = repository;
    }

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = truncate(authentication.getName(), 120);
        String clientIp = resolveClientIp(authentication.getDetails());
        repository.save(new SecurityAuditEvent(Instant.now(), username, SecurityAuditEventType.AUTHENTICATION_SUCCESS,
                clientIp, truncate(MDC.get(CorrelationFilter.TRACE_ID), 64), "Login bem-sucedido"));
    }

    @EventListener
    public void onAuthenticationFailure(AbstractAuthenticationFailureEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication != null ? truncate(authentication.getName(), 120) : "unknown";
        String clientIp = resolveClientIp(authentication != null ? authentication.getDetails() : null);
        String description = event.getException() != null ? event.getException().getMessage() : "Falha de autenticação";
        description = truncate(description, 255);
        repository.save(new SecurityAuditEvent(Instant.now(), username, SecurityAuditEventType.AUTHENTICATION_FAILURE,
                clientIp, truncate(MDC.get(CorrelationFilter.TRACE_ID), 64), description));
    }

    @EventListener
    public void onAuthorizationFailure(AuthorizationFailureEvent event) {
        Authentication authentication = event.getAuthentication();
        String username = authentication != null ? truncate(authentication.getName(), 120) : "anonymous";
        String clientIp = resolveClientIp(authentication != null ? authentication.getDetails() : null);
        String description = truncate("Acesso negado ao recurso " + event.getSource(), 255);
        repository.save(new SecurityAuditEvent(Instant.now(), username, SecurityAuditEventType.AUTHORIZATION_FAILURE,
                clientIp, truncate(MDC.get(CorrelationFilter.TRACE_ID), 64), description));
    }

    private String resolveClientIp(Object details) {
        if (details instanceof WebAuthenticationDetails webDetails) {
            return webDetails.getRemoteAddress();
        }
        return null;
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
