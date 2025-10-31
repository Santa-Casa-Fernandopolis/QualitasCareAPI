package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.domains.Policy;
import com.erp.qualitascareapi.security.domains.PolicyCondition;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class PolicyEvaluator {

    public boolean matchesAll(Policy policy, AuthContext ctx, Object target) {
        for (PolicyCondition c : policy.getConditions()) {
            if (!matches(c, ctx, target)) return false;
        }
        return true;
    }

    private boolean matches(PolicyCondition c, AuthContext ctx, Object target) {
        String type = c.getType();
        String op   = c.getOperator();
        String val  = c.getValue();

        if ("TARGET_DEPARTMENT".equalsIgnoreCase(type)) {
            String targetDept = extractTargetDepartment(target);
            return compare(op, targetDept, val, ctx);
        }
        if ("USER_PROFESSION".equalsIgnoreCase(type)) {
            String prof = extractUserProfession(ctx);
            return compare(op, prof, val, ctx);
        }
        if ("TARGET_STATUS".equalsIgnoreCase(type)) {
            String st = extractTargetStatus(target);
            return compare(op, st, val, ctx);
        }
        if ("TARGET_OWNER_ID".equalsIgnoreCase(type)) {
            String owner = extractTargetOwnerId(target);
            String userId = ctx.userId() == null ? null : String.valueOf(ctx.userId());
            return compare(op, owner, userId, ctx);
        }
        if ("USER_STATUS".equalsIgnoreCase(type)) {
            String status = ctx.status() == null ? null : ctx.status().name();
            return compare(op, status, val, ctx);
        }
        if ("USER_TENANT".equalsIgnoreCase(type)) {
            return compare(op, ctx.tenantIdAsString(), val, ctx);
        }
        if ("USER_ROLE".equalsIgnoreCase(type) || "USER_ROLES".equalsIgnoreCase(type)) {
            return compareSet(op, ctx.roles(), val, ctx);
        }
        if ("USER_ORIGIN".equalsIgnoreCase(type)) {
            String origin = ctx.origin() == null ? null : ctx.origin().name();
            return compare(op, origin, val, ctx);
        }
        if ("USER_ATTRIBUTE".equalsIgnoreCase(type)) {
            String[] kv = val.split("=", 2);
            String attrKey = kv[0];
            String expected = kv.length > 1 ? kv[1] : null;
            return compare(op, ctx.attribute(attrKey), expected, ctx);
        }
        if ("REQUEST_TIME".equalsIgnoreCase(type)) {
            LocalTime now = LocalTime.now();
            return compareTemporal(op, now, val);
        }
        if ("TARGET_TENANT".equalsIgnoreCase(type)) {
            String tenant = extractTargetTenant(target);
            return compare(op, tenant, val, ctx);
        }
        if ("TARGET_TAG".equalsIgnoreCase(type)) {
            Set<String> tags = extractTargetTags(target);
            return compareSet(op, tags, val, ctx);
        }
        return false; // condição desconhecida => seguro
    }

    private boolean compare(String op, String left, String right, AuthContext ctx) {
        right = resolveDynamicToken(right, ctx);
        if ("CURRENT_DEPT".equalsIgnoreCase(right)) {
            right = ctx.department();
        }
        if (left == null) return false;
        if (right == null) return false;

        switch (op.toUpperCase()) {
            case "EQ":     return left.equalsIgnoreCase(right);
            case "NE":     return !left.equalsIgnoreCase(right);
            case "IN": {
                Set<String> set = new HashSet<>(Arrays.asList(right.split("\\|")));
                for (String s : set) if (s.equalsIgnoreCase(left)) return true;
                return false;
            }
            case "NOT_IN": {
                Set<String> set = new HashSet<>(Arrays.asList(right.split("\\|")));
                for (String s : set) if (s.equalsIgnoreCase(left)) return false;
                return true;
            }
            default: return false;
        }
    }

    private boolean compareSet(String op, Set<String> left, String right, AuthContext ctx) {
        Set<String> leftNorm = left == null ? Set.of() : left.stream()
                .filter(Objects::nonNull)
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        String resolved = resolveDynamicToken(right, ctx);
        if (resolved == null) {
            return false;
        }
        Set<String> rightNorm = Arrays.stream(resolved.split("\\|"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .collect(Collectors.toSet());

        switch (op.toUpperCase()) {
            case "IN":
            case "CONTAINS_ANY":
                return rightNorm.stream().anyMatch(leftNorm::contains);
            case "NOT_IN":
            case "CONTAINS_NONE":
                return rightNorm.stream().noneMatch(leftNorm::contains);
            case "CONTAINS_ALL":
                return leftNorm.containsAll(rightNorm);
            case "EQ":
                return rightNorm.size() == 1 && leftNorm.contains(rightNorm.iterator().next());
            default:
                return false;
        }
    }

    private boolean compareTemporal(String op, LocalTime left, String right) {
        if (left == null || right == null) {
            return false;
        }
        String[] parts = right.split("\\|");
        switch (op.toUpperCase()) {
            case "BETWEEN":
                if (parts.length != 2) return false;
                LocalTime start = LocalTime.parse(parts[0]);
                LocalTime end = LocalTime.parse(parts[1]);
                if (start.isBefore(end) || start.equals(end)) {
                    return !left.isBefore(start) && !left.isAfter(end);
                }
                // janela noturna (passagem de dia)
                return !left.isAfter(end) || !left.isBefore(start);
            case "BEFORE":
                return left.isBefore(LocalTime.parse(parts[0]));
            case "AFTER":
                return left.isAfter(LocalTime.parse(parts[0]));
            default:
                return false;
        }
    }

    private String resolveDynamicToken(String value, AuthContext ctx) {
        if (value == null) {
            return null;
        }
        if ("CURRENT_DEPT".equalsIgnoreCase(value)) {
            return ctx.department();
        }
        if ("CURRENT_TENANT".equalsIgnoreCase(value)) {
            return ctx.tenantIdAsString();
        }
        if ("CURRENT_USER_ID".equalsIgnoreCase(value)) {
            return ctx.userId() == null ? null : String.valueOf(ctx.userId());
        }
        if ("CURRENT_PROFESSION".equalsIgnoreCase(value)) {
            return ctx.profession();
        }
        return value;
    }

    private String extractTargetDepartment(Object target) {
        return readProperty(target, "department", "dept", "setor", "sector");
    }

    private String extractTargetStatus(Object target) {
        return readProperty(target, "status", "state", "situacao");
    }

    private String extractTargetOwnerId(Object target) {
        return readProperty(target, "ownerId", "owner", "createdBy");
    }

    private String extractTargetTenant(Object target) {
        return readProperty(target, "tenantId", "tenant", "hospitalId");
    }

    private Set<String> extractTargetTags(Object target) {
        Object value = readRaw(target, "tags", "labels", "attributes");
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        if (value instanceof String str) {
            return Arrays.stream(str.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }

    private String extractUserProfession(AuthContext ctx) { return ctx.profession(); }

    private String readProperty(Object target, String... candidates) {
        Object value = readRaw(target, candidates);
        return value == null ? null : String.valueOf(value);
    }

    private Object readRaw(Object target, String... candidates) {
        if (target == null) {
            return null;
        }
        for (String candidate : candidates) {
            String getter = "get" + capitalize(candidate);
            try {
                Method method = target.getClass().getMethod(getter);
                Object value = method.invoke(target);
                if (value != null) {
                    return value;
                }
            } catch (ReflectiveOperationException ignored) {
            }
            String booleanGetter = "is" + capitalize(candidate);
            try {
                Method method = target.getClass().getMethod(booleanGetter);
                Object value = method.invoke(target);
                if (value != null) {
                    return value;
                }
            } catch (ReflectiveOperationException ignored) {
            }
        }
        return null;
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.length() == 1) {
            return value.substring(0, 1).toUpperCase();
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }
}

