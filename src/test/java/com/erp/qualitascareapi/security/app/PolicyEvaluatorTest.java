package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.security.domains.Policy;
import com.erp.qualitascareapi.security.domains.PolicyCondition;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
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
    void matchesAll_returnsTrueWhenEveryConditionMatches() {
        Policy policy = policyWithConditions(
                condition("TARGET_DEPARTMENT", "EQ", "CURRENT_DEPT"),
                condition("TARGET_TAG", "CONTAINS_ALL", "CRITICAL|UTI"),
                condition("TARGET_OWNER_ID", "EQ", "CURRENT_USER_ID"),
                condition("TARGET_TENANT", "EQ", "CURRENT_TENANT"),
                condition("USER_ATTRIBUTE", "EQ", "shift=DAY")
        );

        AuthContext context = new AuthContext(
                99L,
                "enf.scf",
                1L,
                Set.of("ENFERMEIRO"),
                "UTI",
                "Enfermagem",
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of("shift", "DAY")
        );

        NcTarget target = new NcTarget("UTI", "99", 1L, Set.of("critical", "uti"));

        assertThat(evaluator.matchesAll(policy, context, target)).isTrue();
    }

    @Test
    void matchesAll_returnsFalseWhenAnyConditionFails() {
        Policy policy = policyWithConditions(
                condition("TARGET_TAG", "CONTAINS_NONE", "SENSIVEL"),
                condition("USER_STATUS", "EQ", "ACTIVE")
        );

        AuthContext context = new AuthContext(
                50L,
                "enf.scf",
                1L,
                Set.of("ENFERMEIRO"),
                "UTI",
                "Enfermagem",
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of()
        );

        NcTarget target = new NcTarget("UTI", "50", 1L, Set.of("sensivel", "uti"));

        assertThat(evaluator.matchesAll(policy, context, target)).isFalse();
    }

    @Test
    void shouldMatchWhenUserHasRequiredRoleAndTargetDepartment() {
        Policy policy = new Policy();
        PolicyCondition roleCondition = new PolicyCondition(null, policy, "USER_ROLE", "IN", "ENFERMEIRO");
        PolicyCondition departmentCondition = new PolicyCondition(null, policy, "TARGET_DEPARTMENT", "EQ", "CURRENT_DEPT");
        policy.setConditions(List.of(roleCondition, departmentCondition));

        AuthContext context = new AuthContext(
                10L,
                "enf",
                5L,
                Set.of("ENFERMEIRO"),
                "UTI",
                "Enfermagem",
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of("shift", "DAY")
        );

        NcTarget target = new NcTarget("UTI", "99", 1L, Set.of("critical", "uti"));

        assertThat(evaluator.matchesAll(policy, context, target)).isTrue();
    }

    @Test
    void shouldFailWhenTargetTagsDoNotContainAllRequiredValues() {
        Policy policy = new Policy();
        PolicyCondition tagsCondition = new PolicyCondition(null, policy, "TARGET_TAG", "CONTAINS_ALL", "URGENTE|CRITICO");
        policy.setConditions(List.of(tagsCondition));

        AuthContext context = new AuthContext(
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

        NcTarget target = new NcTarget("UTI", "50", 1L, Set.of("sensivel", "alto_risco"));

        assertThat(evaluator.matchesAll(policy, context, target)).isFalse();
    }

    @Test
    void shouldResolveDynamicTokensForCurrentUserId() {
        Policy policy = new Policy();
        PolicyCondition condition = new PolicyCondition(null, policy, "TARGET_OWNER_ID", "EQ", "CURRENT_USER_ID");
        policy.setConditions(List.of(condition));

        AuthContext context = new AuthContext(
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

        assertThat(evaluator.matchesAll(policy, context, target)).isTrue();
    }

    @Test
    void shouldAllowNotInWhenTargetAttributeMissing() {
        Policy policy = policyWithConditions(condition("TARGET_STATUS", "NOT_IN", "INATIVO"));

        AuthContext context = new AuthContext(
                17L,
                "user",
                3L,
                Set.of("MEDICO"),
                "CARDIO",
                "Cardiologia",
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of()
        );

        Object targetWithoutStatus = new Object();

        assertThat(evaluator.matchesAll(policy, context, targetWithoutStatus)).isTrue();
    }

    @Test
    void shouldTreatNullRoleConditionAsNoRestrictionForNotIn() {
        Policy policy = policyWithConditions(condition("USER_ROLE", "NOT_IN", null));

        AuthContext context = new AuthContext(
                33L,
                "user",
                2L,
                Set.of("MEDICO"),
                "CARDIO",
                "Cardiologia",
                UserStatus.ACTIVE,
                IdentityOrigin.LOCAL,
                Map.of()
        );

        assertThat(evaluator.matchesAll(policy, context, new Object())).isTrue();
    }

    private Policy policyWithConditions(PolicyCondition... conditions) {
        Policy policy = new Policy();
        List<PolicyCondition> list = new ArrayList<>();
        for (PolicyCondition condition : conditions) {
            condition.setPolicy(policy);
            list.add(condition);
        }
        policy.setConditions(list);
        return policy;
    }

    private PolicyCondition condition(String type, String operator, String value) {
        return new PolicyCondition(null, null, type, operator, value);
    }

    private static final class NcTarget {
        private final String department;
        private final String ownerId;
        private final Long tenantId;
        private final Set<String> tags;

        private NcTarget(String department, String ownerId, Long tenantId, Set<String> tags) {
            this.department = department;
            this.ownerId = ownerId;
            this.tenantId = tenantId;
            this.tags = new HashSet<>(tags);
        }

        public String getDepartment() {
            return department;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public Long getTenantId() {
            return tenantId;
        }

        public Set<String> getTags() {
            return tags;
        }
    }
}
