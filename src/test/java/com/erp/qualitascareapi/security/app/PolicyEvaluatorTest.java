package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.domains.Policy;
import com.erp.qualitascareapi.security.domains.PolicyCondition;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyEvaluatorTest {

    private PolicyEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new PolicyEvaluator();
    }

    @Test
    void shouldMatchWhenUserHasRequiredRoleAndTargetDepartment() {
        Policy policy = new Policy();
        PolicyCondition roleCondition = new PolicyCondition(null, policy, "USER_ROLE", "IN", "ENFERMEIRO");
        PolicyCondition departmentCondition = new PolicyCondition(null, policy, "TARGET_DEPARTMENT", "EQ", "CURRENT_DEPT");
        policy.setConditions(List.of(roleCondition, departmentCondition));

        AuthContext ctx = new AuthContext(
                10L,
                "enf",
                5L,
                Set.of("ENFERMEIRO"),
                "UTI",
                "Enfermagem",
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of()
        );

        Object target = new Object() {
            public String getDepartment() {
                return "UTI";
            }
        };

        assertThat(evaluator.matchesAll(policy, ctx, target)).isTrue();
    }

    @Test
    void shouldFailWhenTargetTagsDoNotContainAllRequiredValues() {
        Policy policy = new Policy();
        PolicyCondition tagsCondition = new PolicyCondition(null, policy, "TARGET_TAG", "CONTAINS_ALL", "URGENTE|CRITICO");
        policy.setConditions(List.of(tagsCondition));

        AuthContext ctx = new AuthContext(
                10L,
                "enf",
                5L,
                Set.of("ENFERMEIRO"),
                "UTI",
                "Enfermagem",
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of()
        );

        Object target = new Object() {
            public Set<String> getTags() {
                return Set.of("URGENTE");
            }
        };

        assertThat(evaluator.matchesAll(policy, ctx, target)).isFalse();
    }

    @Test
    void shouldResolveDynamicTokensForCurrentUserId() {
        Policy policy = new Policy();
        PolicyCondition condition = new PolicyCondition(null, policy, "TARGET_OWNER_ID", "EQ", "CURRENT_USER_ID");
        policy.setConditions(List.of(condition));

        AuthContext ctx = new AuthContext(
                42L,
                "auditor",
                7L,
                Set.of("AUDITOR"),
                null,
                null,
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of()
        );

        Object target = new Object() {
            public Long getOwnerId() {
                return 42L;
            }
        };

        assertThat(evaluator.matchesAll(policy, ctx, target)).isTrue();
    }
}
