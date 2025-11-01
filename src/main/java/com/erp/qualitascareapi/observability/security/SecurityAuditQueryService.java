package com.erp.qualitascareapi.observability.security;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class SecurityAuditQueryService {

    private final SecurityAuditEventRepository repository;

    public SecurityAuditQueryService(SecurityAuditEventRepository repository) {
        this.repository = repository;
    }

    public Page<SecurityAuditEvent> search(SecurityAuditEventFilter filter, Pageable pageable) {
        return repository.findAll((root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            filter.from().ifPresent(from -> predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), from)));
            filter.to().ifPresent(to -> predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), to)));
            filter.username().ifPresent(username -> predicates.add(cb.equal(root.get("username"), username)));
            filter.eventType().ifPresent(eventType -> predicates.add(cb.equal(root.get("eventType"), eventType)));
            filter.traceId().ifPresent(traceId -> predicates.add(cb.equal(root.get("traceId"), traceId)));

            return cb.and(predicates.toArray(Predicate[]::new));
        }, pageable);
    }
}
