package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.domains.Policy;
import com.erp.qualitascareapi.security.domains.PolicyCondition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
            return compare(op, owner, String.valueOf(ctx.userId()), ctx);
        }
        return false; // condição desconhecida => seguro
    }

    private boolean compare(String op, String left, String right, AuthContext ctx) {
        if ("CURRENT_DEPT".equalsIgnoreCase(right)) {
            right = ctx.department();
        }
        if (left == null) return false;

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

    // Stubs (implemente conforme seus DTOs/entidades-alvo)
    private String extractTargetDepartment(Object target) { return null; }
    private String extractTargetStatus(Object target) { return null; }
    private String extractTargetOwnerId(Object target) { return null; }
    private String extractUserProfession(AuthContext ctx) { return null; }
}

