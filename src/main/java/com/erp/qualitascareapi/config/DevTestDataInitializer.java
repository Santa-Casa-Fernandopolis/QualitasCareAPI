package com.erp.qualitascareapi.config;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.domain.Permission;
import com.erp.qualitascareapi.security.domain.Policy;
import com.erp.qualitascareapi.security.domain.PolicyCondition;
import com.erp.qualitascareapi.security.domain.Role;
import com.erp.qualitascareapi.security.domain.RolePermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.enums.UserStatus;
import com.erp.qualitascareapi.security.repo.PermissionRepository;
import com.erp.qualitascareapi.security.repo.PolicyRepository;
import com.erp.qualitascareapi.security.repo.RolePermissionRepository;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
@Profile({"dev", "test"})
public class DevTestDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevTestDataInitializer.class);

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PolicyRepository policyRepository;
    private final PasswordEncoder passwordEncoder;

    public DevTestDataInitializer(TenantRepository tenantRepository,
                                  UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  PermissionRepository permissionRepository,
                                  RolePermissionRepository rolePermissionRepository,
                                  PolicyRepository policyRepository,
                                  PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.policyRepository = policyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (tenantRepository.count() > 0) {
            log.info("Skipping dev/test data initialization because the database is not empty.");
            return;
        }

        log.info("Bootstrapping reference data for dev/test environment...");

        Tenant scf = tenantRepository.save(new Tenant(null, "SCF", "Santa Casa Felicidade", true));
        Tenant scj = tenantRepository.save(new Tenant(null, "SCJ", "Santa Casa Jacarandá", true));

        Role scfSystemAdmin = roleRepository.save(new Role(null, "SYSTEM_ADMIN", scf, "Administrador do sistema"));
        Role scfAdmin = roleRepository.save(new Role(null, "ADMIN_QUALIDADE", scf, "Administrador de Qualidade"));
        Role scfNurse = roleRepository.save(new Role(null, "ENFERMEIRO", scf, "Profissional de enfermagem"));
        Role scjSystemAdmin = roleRepository.save(new Role(null, "SYSTEM_ADMIN", scj, "Administrador do sistema"));
        Role scjAdmin = roleRepository.save(new Role(null, "ADMIN_QUALIDADE", scj, "Administrador de Qualidade"));
        Role scjNurse = roleRepository.save(new Role(null, "ENFERMEIRO", scj, "Profissional de enfermagem"));

        Permission scfNcRead = permissionRepository.save(
                new Permission(null, ResourceType.NC, Action.READ, "LISTA", scf, "NC_READ@LISTA"));
        Permission scfNcCreate = permissionRepository.save(
                new Permission(null, ResourceType.NC, Action.CREATE, null, scf, "NC_CREATE"));
        Permission scjNcRead = permissionRepository.save(
                new Permission(null, ResourceType.NC, Action.READ, "LISTA", scj, "NC_READ@LISTA"));
        Permission scjNcCreate = permissionRepository.save(
                new Permission(null, ResourceType.NC, Action.CREATE, null, scj, "NC_CREATE"));

        rolePermissionRepository.saveAll(List.of(
                new RolePermission(null, scfSystemAdmin, scfNcRead, scf),
                new RolePermission(null, scfSystemAdmin, scfNcCreate, scf),
                new RolePermission(null, scfAdmin, scfNcRead, scf),
                new RolePermission(null, scfAdmin, scfNcCreate, scf),
                new RolePermission(null, scfNurse, scfNcRead, scf),
                new RolePermission(null, scjSystemAdmin, scjNcRead, scj),
                new RolePermission(null, scjSystemAdmin, scjNcCreate, scj),
                new RolePermission(null, scjAdmin, scjNcRead, scj),
                new RolePermission(null, scjAdmin, scjNcCreate, scj),
                new RolePermission(null, scjNurse, scjNcRead, scj)
        ));

        policyRepository.save(buildPolicy(
                scf,
                Set.of(scfAdmin),
                ResourceType.NC,
                Action.UPDATE,
                null,
                Effect.ALLOW,
                5,
                "Admin pode atualizar qualquer NC"
        ));

        Policy scfNursePolicy = buildPolicy(
                scf,
                Set.of(scfNurse),
                ResourceType.NC,
                Action.READ,
                "LISTA",
                Effect.ALLOW,
                10,
                "Enfermeiros visualizam NC apenas do próprio setor"
        );
        scfNursePolicy.getConditions().add(new PolicyCondition(null, scfNursePolicy,
                "TARGET_DEPARTMENT", "EQ", "CURRENT_DEPT"));
        policyRepository.save(scfNursePolicy);

        policyRepository.save(buildPolicy(
                scj,
                Set.of(scjAdmin),
                ResourceType.NC,
                Action.APPROVE,
                null,
                Effect.ALLOW,
                5,
                "Admin da SCJ aprova qualquer NC"
        ));

        Policy scjNursePolicy = buildPolicy(
                scj,
                Set.of(scjNurse),
                ResourceType.NC,
                Action.READ,
                "LISTA",
                Effect.ALLOW,
                10,
                "Enfermeiros da SCJ visualizam NC do setor"
        );
        scjNursePolicy.getConditions().add(new PolicyCondition(null, scjNursePolicy,
                "TARGET_DEPARTMENT", "EQ", "CURRENT_DEPT"));
        policyRepository.save(scjNursePolicy);

        LocalDateTime now = LocalDateTime.now();
        List<String> createdUsers = new ArrayList<>();

        createUserIfMissing("sys.scf", "SysAdmin SCF", "TI", scf, scfSystemAdmin, now, createdUsers);
        createUserIfMissing("admin.scf", "Admin SCF", "Qualidade", scf, scfAdmin, now, createdUsers);
        createUserIfMissing("enf.scf", "Enfermeira SCF", "UTI", scf, scfNurse, now, createdUsers);
        createUserIfMissing("sys.scj", "SysAdmin SCJ", "TI", scj, scjSystemAdmin, now, createdUsers);
        createUserIfMissing("admin.scj", "Admin SCJ", "Qualidade", scj, scjAdmin, now, createdUsers);
        createUserIfMissing("enf.scj", "Enfermeira SCJ", "Pronto Atendimento", scj, scjNurse, now, createdUsers);

        if (createdUsers.isEmpty()) {
            log.info("Dev/test data initialization finished. Nenhum usuário novo criado.");
        } else {
            log.info("Dev/test data initialization finished. Users criados: {}", String.join(", ", createdUsers));
        }
    }

    private Policy buildPolicy(Tenant tenant,
                               Set<Role> roles,
                               ResourceType resource,
                               Action action,
                               String feature,
                               Effect effect,
                               int priority,
                               String description) {
        Policy policy = new Policy(null, tenant, resource, action, feature, effect, true, priority, description);
        policy.setRoles(roles);
        return policy;
    }

    private Role findOrCreateRole(Tenant tenant, String name, String description) {
        return roleRepository.findByNameIgnoreCaseAndTenant_Id(name, tenant.getId())
                .orElseGet(() -> roleRepository.save(new Role(null, name, tenant, description)));
    }

    private Permission findOrCreatePermission(Tenant tenant,
                                              ResourceType resource,
                                              Action action,
                                              String feature,
                                              String code) {
        return permissionRepository.findByTenant_IdAndResourceAndActionAndFeature(tenant.getId(), resource, action, feature)
                .orElseGet(() -> permissionRepository.save(new Permission(null, resource, action, feature, tenant, code)));
    }

    private void ensureRolePermission(Role role, Permission permission, Tenant tenant) {
        if (!rolePermissionRepository.existsByRoleAndPermissionAndTenant(role, permission, tenant)) {
            rolePermissionRepository.save(new RolePermission(null, role, permission, tenant));
        }
    }

    private void createUserIfMissing(String username,
                                     String fullName,
                                     String department,
                                     Tenant tenant,
                                     Role role,
                                     LocalDateTime referenceTime,
                                     List<String> createdUsers) {
        if (userRepository.findByUsernameIgnoreCase(username).isPresent()) {
            log.debug("Usuário {} já existe - pulando criação.", username);
            return;
        }

        String defaultPassword = defaultPasswordFor(username);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(defaultPassword));
        user.setFullName(fullName);
        user.setDepartment(department);
        user.setTenant(tenant);
        user.setStatus(UserStatus.ACTIVE);
        user.setOrigin(IdentityOrigin.LOCAL);
        user.setActivatedAt(referenceTime);
        user.setExpiresAt(referenceTime.plusYears(1));
        user.getRoles().add(role);
        userRepository.save(user);
        createdUsers.add(username + "/" + defaultPassword);
    }

    private String defaultPasswordFor(String username) {
        if (username.startsWith("admin")) {
            return "admin123";
        }
        if (username.startsWith("enf")) {
            return "enf123";
        }
        if (username.startsWith("sys")) {
            return "sys123";
        }
        return "changeme";
    }
}
