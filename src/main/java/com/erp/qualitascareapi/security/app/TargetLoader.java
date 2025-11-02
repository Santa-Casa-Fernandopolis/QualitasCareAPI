package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.app.target.TargetNotFoundException;
import com.erp.qualitascareapi.security.app.target.TargetResolutionException;
import com.erp.qualitascareapi.security.app.target.TargetResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class TargetLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(TargetLoader.class);
    private static final String CACHE_ATTRIBUTE = TargetLoader.class.getName() + ".CACHE";
    private static final Object NOT_FOUND = new Object();

    private final Map<String, TargetResolver<?>> resolversByType;

    public TargetLoader(List<TargetResolver<?>> resolvers) {
        if (resolvers == null || resolvers.isEmpty()) {
            this.resolversByType = Map.of();
            return;
        }
        this.resolversByType = resolvers.stream()
                .collect(Collectors.toUnmodifiableMap(
                        resolver -> normalize(resolver.targetType()),
                        resolver -> resolver,
                        (left, right) -> {
                            throw new IllegalStateException("More than one TargetResolver registered for the same type");
                        }));
    }

    public Object load(String targetType, Serializable targetId, AuthContext context) {
        String normalizedType = normalize(targetType);
        if (normalizedType == null) {
            throw new TargetResolutionException("Target type must be provided");
        }
        if (targetId == null) {
            throw new TargetResolutionException("Target identifier must be provided for type " + normalizedType);
        }
        TargetResolver<?> resolver = resolversByType.get(normalizedType);
        if (resolver == null) {
            throw new TargetResolutionException("Unsupported target type: " + normalizedType);
        }
        if (context == null || context.tenantId() == null) {
            throw new TargetResolutionException("Tenant context is required to resolve target type " + normalizedType);
        }

        Cache cache = Cache.obtain();
        CacheKey cacheKey = new CacheKey(normalizedType, targetId, context.tenantId());
        if (cache != null) {
            Object cached = cache.get(cacheKey);
            if (cached != null) {
                if (cached == NOT_FOUND) {
                    throw notFound(normalizedType, targetId, context, cache, cacheKey);
                }
                return cached;
            }
        }

        Object target = resolver.resolve(targetId, context)
                .orElseThrow(() -> notFound(normalizedType, targetId, context, cache, cacheKey));

        if (cache != null) {
            cache.put(cacheKey, target);
        }
        LOGGER.debug("Target resolved type={} id={} tenant={} via={}",
                normalizedType, targetId, context.tenantId(), resolver.getClass().getSimpleName());
        return target;
    }

    private TargetNotFoundException notFound(String type, Serializable id, AuthContext context, Cache cache, CacheKey cacheKey) {
        LOGGER.warn("Target not found type={} id={} tenant={}", type, id, context.tenantId());
        if (cache != null) {
            cache.put(cacheKey, NOT_FOUND);
        }
        return new TargetNotFoundException(type, id);
    }

    @Nullable
    private static String normalize(String targetType) {
        if (targetType == null || targetType.isBlank()) {
            return null;
        }
        return targetType.trim().toUpperCase(Locale.ROOT);
    }

    private record CacheKey(String targetType, Serializable targetId, Long tenantId) {
        private CacheKey {
            Objects.requireNonNull(targetType, "targetType");
            Objects.requireNonNull(targetId, "targetId");
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return targetType.equals(cacheKey.targetType)
                    && Objects.equals(String.valueOf(targetId), String.valueOf(cacheKey.targetId))
                    && Objects.equals(tenantId, cacheKey.tenantId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(targetType, String.valueOf(targetId), tenantId);
        }
    }

    private static final class Cache {
        private final Map<CacheKey, Object> delegate;

        private Cache(Map<CacheKey, Object> delegate) {
            this.delegate = delegate;
        }

        static Cache obtain() {
            RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return null;
            }
            @SuppressWarnings("unchecked")
            Map<CacheKey, Object> map = (Map<CacheKey, Object>) attributes.getAttribute(CACHE_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
            if (map == null) {
                map = new ConcurrentHashMap<>();
                attributes.setAttribute(CACHE_ATTRIBUTE, map, RequestAttributes.SCOPE_REQUEST);
            }
            return new Cache(map);
        }

        Object get(CacheKey key) {
            return delegate.get(key);
        }

        void put(CacheKey key, Object value) {
            delegate.put(key, value);
        }
    }
}

