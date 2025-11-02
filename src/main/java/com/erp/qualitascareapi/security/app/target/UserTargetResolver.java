package com.erp.qualitascareapi.security.app.target;

import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.iam.repo.projection.UserAuthorizationProjection;
import com.erp.qualitascareapi.security.app.AuthContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;

@Component
public class UserTargetResolver implements TargetResolver<UserTarget> {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserTargetResolver.class);

    private final UserRepository userRepository;

    public UserTargetResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public String targetType() {
        return "USER";
    }

    @Override
    public Optional<UserTarget> resolve(Serializable targetId, AuthContext context) {
        Long userId = toLong(targetId);
        Long tenantId = context.tenantId();
        Optional<UserAuthorizationProjection> projection =
                userRepository.findAuthorizationProjectionByIdAndTenantId(userId, tenantId);
        projection.ifPresent(p -> LOGGER.debug("Resolved user target id={} tenant={} department={}",
                p.getId(), p.getTenantId(), p.getDepartment()));
        return projection.map(UserTargetResolver::toTarget);
    }

    private static Long toLong(Serializable targetId) {
        if (targetId instanceof Long l) {
            return l;
        }
        if (targetId instanceof Number number) {
            return number.longValue();
        }
        if (targetId instanceof String str) {
            try {
                return Long.parseLong(str);
            } catch (NumberFormatException ex) {
                throw new TargetResolutionException("Invalid numeric identifier for USER target: " + str, ex);
            }
        }
        throw new TargetResolutionException("Unsupported identifier type for USER target: " + targetId.getClass().getName());
    }

    private static UserTarget toTarget(UserAuthorizationProjection projection) {
        return new UserTarget(
                projection.getId(),
                projection.getTenantId(),
                projection.getDepartment(),
                projection.getStatus(),
                projection.getOrigin(),
                projection.getUsername()
        );
    }
}
