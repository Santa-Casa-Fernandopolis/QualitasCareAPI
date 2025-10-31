package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.domains.Policy;
import com.erp.qualitascareapi.security.domains.PolicyCondition;
import com.erp.qualitascareapi.security.domains.Role;
import com.erp.qualitascareapi.security.domains.UserPermissionOverride;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.repo.PolicyRepository;
import com.erp.qualitascareapi.security.repo.UserPermissionOverrideRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccessDecisionServiceIntegrationTest {

    @Autowired
    private AccessDecisionService accessDecisionService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private UserPermissionOverrideRepository overrideRepository;

    @Test
    void adminShouldUpdateNcViaPolicy() {
        User admin = userRepository.findByUsernameIgnoreCase("admin.scf")
                .orElseThrow();

        AuthContext ctx = authContextFrom(admin);

        boolean allowed = accessDecisionService.isAllowed(ctx, ResourceType.NC, Action.UPDATE, null, null);

        assertThat(allowed).isTrue();
    }

    @Test
    void nurseShouldBeDeniedNcUpdateWithoutOverride() {
        User nurse = userRepository.findByUsernameIgnoreCase("enf.scf")
                .orElseThrow();

        AuthContext ctx = authContextFrom(nurse);

        boolean allowed = accessDecisionService.isAllowed(ctx, ResourceType.NC, Action.UPDATE, null, null);

        assertThat(allowed).isFalse();
    }

    @Test
    void nurseOverrideShouldGrantTemporaryUpdateAccess() {
        User nurse = userRepository.findByUsernameIgnoreCase("enf.scf")
                .orElseThrow();

        UserPermissionOverride override = new UserPermissionOverride(
                null,
                nurse,
                nurse.getTenant(),
                ResourceType.NC,
                Action.UPDATE,
                null,
                Effect.ALLOW,
                1,
                "Temporary maintenance window"
        );
        override.setApproved(true);
        override.setValidFrom(LocalDateTime.now().minusMinutes(5));
        override.setValidUntil(LocalDateTime.now().plusMinutes(5));
        overrideRepository.saveAndFlush(override);

        AuthContext ctx = authContextFrom(nurse);

        boolean allowed = accessDecisionService.isAllowed(ctx, ResourceType.NC, Action.UPDATE, null, null);

        assertThat(allowed).isTrue();
    }

    @Test
    void policyConditionShouldAllowUpdateWhenTargetMatchesContext() {
        User nurse = userRepository.findByUsernameIgnoreCase("enf.scf")
                .orElseThrow();

        Policy policy = new Policy(
                null,
                nurse.getTenant(),
                ResourceType.NC,
                Action.UPDATE,
                null,
                Effect.ALLOW,
                true,
                7,
                "Nurse can update own department"
        );
        policy.setRoles(new HashSet<>(nurse.getRoles()));
        PolicyCondition condition = new PolicyCondition(null, policy, "TARGET_DEPARTMENT", "EQ", "CURRENT_DEPT");
        policy.getConditions().add(condition);
        policyRepository.saveAndFlush(policy);

        AuthContext ctx = authContextFrom(nurse);
        NcRecord target = new NcRecord(nurse.getDepartment());

        boolean allowed = accessDecisionService.isAllowed(ctx, ResourceType.NC, Action.UPDATE, null, target);

        assertThat(allowed).isTrue();
    }

    private AuthContext authContextFrom(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(java.util.stream.Collectors.toSet());
        return new AuthContext(
                user.getId(),
                user.getUsername(),
                user.getTenant().getId(),
                roleNames,
                user.getDepartment(),
                null,
                user.getStatus(),
                user.getOrigin(),
                Map.of()
        );
    }

    private record NcRecord(String department) {
        public String getDepartment() {
            return department;
        }
    }
}
