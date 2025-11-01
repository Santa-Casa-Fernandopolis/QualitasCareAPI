package com.erp.qualitascareapi.observability.logging;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class RequestLogQueryService {

    private final RequestLogRepository repository;

    public RequestLogQueryService(RequestLogRepository repository) {
        this.repository = repository;
    }

    public Page<RequestLog> search(RequestLogFilter filter, Pageable pageable) {
        return repository.findAll((root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            filter.fromOptional().ifPresent(from -> predicates.add(cb.greaterThanOrEqualTo(root.get("timestamp"), from)));
            filter.toOptional().ifPresent(to -> predicates.add(cb.lessThanOrEqualTo(root.get("timestamp"), to)));
            filter.methodOptional().ifPresent(method -> predicates.add(cb.equal(root.get("method"), method)));
            filter.statusOptional().ifPresent(status -> predicates.add(cb.equal(root.get("status"), status)));
            filter.userIdOptional().ifPresent(userId -> predicates.add(cb.equal(root.get("userId"), userId)));
            filter.traceIdOptional().ifPresent(traceId -> predicates.add(cb.equal(root.get("traceId"), traceId)));
            filter.pathOptional().ifPresent(path -> predicates.add(cb.like(cb.lower(root.get("path")), "%" + path.toLowerCase() + "%")));

            return cb.and(predicates.toArray(Predicate[]::new));
        }, pageable);
    }
}
