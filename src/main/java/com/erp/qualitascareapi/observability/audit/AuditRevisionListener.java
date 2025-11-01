package com.erp.qualitascareapi.observability.audit;

import com.erp.qualitascareapi.observability.logging.CorrelationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuditRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        AuditRevisionEntity revision = (AuditRevisionEntity) revisionEntity;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        revision.setUsername(authentication != null ? authentication.getName() : "system");
        revision.setClientIp(resolveClientIp());
    }

    private String resolveClientIp() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            HttpServletRequest request = servletRequestAttributes.getRequest();
            Object attribute = request.getAttribute(CorrelationFilter.CLIENT_IP);
            if (attribute instanceof String clientIp) {
                return clientIp;
            }
            return request.getRemoteAddr();
        }
        return null;
    }
}
