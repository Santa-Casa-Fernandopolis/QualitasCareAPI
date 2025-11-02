package com.erp.qualitascareapi.security.app.target;

import com.erp.qualitascareapi.security.app.AuthContext;

import java.io.Serializable;
import java.util.Optional;

/**
 * Strategy interface used by {@link com.erp.qualitascareapi.security.app.TargetLoader}
 * to retrieve lightweight projections of domain objects for authorization decisions.
 */
public interface TargetResolver<T> {

    /**
     * @return target type handled by this resolver (case-insensitive).
     */
    String targetType();

    /**
     * Resolve the target instance given its identifier and current authenticated context.
     *
     * @param targetId identifier received from the security annotation
     * @param context  authenticated user context (always includes tenant information)
     * @return optional containing the resolved target or empty if not found
     */
    Optional<T> resolve(Serializable targetId, AuthContext context);
}
