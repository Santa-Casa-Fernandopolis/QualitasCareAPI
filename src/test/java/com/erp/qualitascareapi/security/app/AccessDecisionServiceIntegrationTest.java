package com.erp.qualitascareapi.security.app;

import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.domains.UserPermissionOverride;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.repo.UserPermissionOverrideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private UserPermissionOverrideRepository overrideRepository;

    private User nurseScf;
    private User adminScf;

    @BeforeEach
    void loadReferenceUsers() {
        nurseScf = userRepository.findByUsernameIgnoreCase("enf.scf")
                .orElseThrow(() -> new IllegalStateException("Seed user enf.scf not found"));
        adminScf = userRepository.findByUsernameIgnoreCase("admin.scf")
                .orElseThrow(() -> new IllegalStateException("Seed user admin.scf not found"));
    }

    @Test
    void nurseCanReadListWithinOwnDepartmentThroughPolicy() {
        AuthContext context = authContextFrom(nurseScf);
        NcProjection target = new NcProjection(nurseScf.getDepartment(), nurseScf.getTenant().getId(), String.valueOf(nurseScf.getId()));

        boolean allowed = accessDecisionService.isAllowed(context, ResourceType.NC, Action.READ, "LISTA", target);

        assertThat(allowed).isTrue();
    }

    @Test
    void nurseCannotReadListFromDifferentDepartment() {
        AuthContext context = authContextFrom(nurseScf);
        NcProjection target = new NcProjection("Pronto Atendimento", nurseScf.getTenant().getId(), String.valueOf(nurseScf.getId()));

        boolean allowed = accessDecisionService.isAllowed(context, ResourceType.NC, Action.READ, "LISTA", target);

        assertThat(allowed).isFalse();
    }

    @Test
    void adminReceivesRbacGrantForCreateAction() {
        AuthContext context = authContextFrom(adminScf);

        boolean allowed = accessDecisionService.isAllowed(context, ResourceType.NC, Action.CREATE, null, null);

        assertThat(allowed).isTrue();
    }

    @Test
    void explicitDenyOverrideTakesPrecedenceOverPolicyAllow() {
        AuthContext context = authContextFrom(nurseScf);

        UserPermissionOverride deny = new UserPermissionOverride();
        deny.setUser(nurseScf);
        deny.setTenant(nurseScf.getTenant());
        deny.setResource(ResourceType.NC);
        deny.setAction(Action.READ);
        deny.setFeature("LISTA");
        deny.setEffect(Effect.DENY);
        deny.setPriority(1);
        deny.setReason("Temporary restriction");
        deny.setApproved(true);
        deny.setApprovedBy("system");
        deny.setApprovedAt(LocalDateTime.now());
        deny.setValidFrom(LocalDateTime.now().minusDays(1));
        deny.setValidUntil(LocalDateTime.now().plusDays(1));
        overrideRepository.saveAndFlush(deny);

        NcProjection target = new NcProjection(nurseScf.getDepartment(), nurseScf.getTenant().getId(), String.valueOf(nurseScf.getId()));
        boolean allowed = accessDecisionService.isAllowed(context, ResourceType.NC, Action.READ, "LISTA", target);

        assertThat(allowed).isFalse();
    }

    private AuthContext authContextFrom(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().toUpperCase())
                .collect(Collectors.toUnmodifiableSet());
        return new AuthContext(
                user.getId(),
                user.getUsername(),
                user.getTenant().getId(),
                roles,
                user.getDepartment(),
                null,
                user.getStatus(),
                user.getOrigin(),
                Map.of()
        );
    }

    private static final class NcProjection {
        private final String department;
        private final Long tenantId;
        private final String ownerId;

        private NcProjection(String department, Long tenantId, String ownerId) {
            this.department = department;
            this.tenantId = tenantId;
            this.ownerId = ownerId;
        }

        public String getDepartment() {
            return department;
        }

        public Long getTenantId() {
            return tenantId;
        }

        public String getOwnerId() {
            return ownerId;
        }
    }
}
