package com.erp.qualitascareapi.config;

import com.erp.qualitascareapi.common.vo.PeriodoVigencia;
import com.erp.qualitascareapi.iam.domain.OrgRoleAssignment;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import com.erp.qualitascareapi.iam.enums.TipoSetor;
import com.erp.qualitascareapi.iam.repo.OrgRoleAssignmentRepository;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.domain.*;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.security.enums.UserStatus;
import com.erp.qualitascareapi.security.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
    private final UserPermissionOverrideRepository userPermissionOverrideRepository;
    private final SetorRepository setorRepository;
    private final OrgRoleAssignmentRepository orgRoleAssignmentRepository;

    public DevTestDataInitializer(TenantRepository tenantRepository,
                                  UserRepository userRepository,
                                  RoleRepository roleRepository,
                                  PermissionRepository permissionRepository,
                                  RolePermissionRepository rolePermissionRepository,
                                  PolicyRepository policyRepository,
                                  UserPermissionOverrideRepository  userPermissionOverrideRepository,
                                  SetorRepository setorRepository,
                                  OrgRoleAssignmentRepository orgRoleAssignmentRepository,
                                  PasswordEncoder passwordEncoder) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.policyRepository = policyRepository;
        this.userPermissionOverrideRepository = userPermissionOverrideRepository;
        this.setorRepository = setorRepository;
        this.orgRoleAssignmentRepository = orgRoleAssignmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (tenantRepository.count() > 0) {
            log.info("Skipping dev/test data initialization because the database is not empty.");
            return;
        }

        log.info("Bootstrapping reference data for dev/test environment...");

        Tenant scf = tenantRepository.save(new Tenant(null, 1001L, "Santa Casa Felicidade",
                "12345678000100", "https://cdn.qualitascare.com/logos/scf.png", true));
        Tenant scj = tenantRepository.save(new Tenant(null, 1002L, "Santa Casa Jacarandá",
                "12345678000290", "https://cdn.qualitascare.com/logos/scj.png", true));


        // Dentro do seu DevTestDataInitializer (run), depois de criar os Tenants scf e scj

        // ===== IAM ROLES – SCF =====
        Role scfSystemAdmin = roleRepository.save(new Role(null, "SYSTEM_ADMIN", scf, "Administrador do sistema"));
        Role scfAdminTI = roleRepository.save(new Role(null, "ADMIN_TI", scf, "Administrador do TI"));
        Role scfAdminQualidade = roleRepository.save(new Role(null, "ADMIN_QUALIDADE", scf, "Admin de Qualidade e Documentos"));
        Role scfAdminAssistencial = roleRepository.save(new Role(null, "ADMIN_ASSISTENCIAL", scf, "Admin de módulos assistenciais (CME, UTI, etc.)"));
        Role scfGestor = roleRepository.save(new Role(null, "GESTOR", scf, "Gestor assistencial (nível gerência)"));
        Role scfOperador = roleRepository.save(new Role(null, "OPERADOR", scf, "Operador dos módulos assistenciais"));
        Role scfLeitor = roleRepository.save(new Role(null, "LEITOR", scf, "Somente leitura"));


        // ===== IAM ROLES – SCJ (mesma tipologia, outro tenant) =====
        Role scjSystemAdmin = roleRepository.save(new Role(null, "SYSTEM_ADMIN", scj, "Administrador do sistema"));
        Role scjAdminTI = roleRepository.save(new Role(null, "ADMIN_TI", scj, "Administrador do TI"));
        Role scjAdminQualidade = roleRepository.save(new Role(null, "ADMIN_QUALIDADE", scj, "Admin de Qualidade e Documentos"));
        Role scjAdminAssistencial = roleRepository.save(new Role(null, "ADMIN_ASSISTENCIAL", scj, "Admin de módulos assistenciais (CME, UTI, etc.)"));
        Role scjGestor = roleRepository.save(new Role(null, "GESTOR", scj, "Gestor assistencial (nível gerência)"));
        Role scjOperador = roleRepository.save(new Role(null, "OPERADOR", scj, "Operador dos módulos assistenciais"));
        Role scjLeitor = roleRepository.save(new Role(null, "LEITOR", scj, "Somente leitura"));



        LocalDateTime now = LocalDateTime.now();
        List<String> createdUsers = new ArrayList<>();

        // SCF
        createUserIfMissing("jefferson.passerini.scf", "SysAdmin SCF",        "TI",              scf, scfSystemAdmin,      now, createdUsers);
        createUserIfMissing("admin.ti.scf",   "Admin TI SCF", "TI", scf, scfAdminTI, now, createdUsers);
        createUserIfMissing("admin.qual.scf",   "Admin Qualidade SCF", "Qualidade",       scf, scfAdminQualidade,   now, createdUsers);
        createUserIfMissing("admin.assist.scf", "Admin Assistencial",  "Gerência Assist", scf, scfAdminAssistencial,now, createdUsers);
        createUserIfMissing("gestor.enf.scf",   "Gerente Enfermagem",  "Enfermagem",      scf, scfGestor,           now, createdUsers);
        createUserIfMissing("sup.cme.scf",      "Supervisora CME",     "CME",             scf, scfOperador,         now, createdUsers);
        createUserIfMissing("enf.cme.scf",      "Enfermeira CME",      "CME",             scf, scfOperador,         now, createdUsers);

        // SCJ
        createUserIfMissing("jefferson.passerini.scj", "SysAdmin SCJ",        "TI",              scj, scjSystemAdmin,      now, createdUsers);
        createUserIfMissing("admin.ti.scj",   "Admin TI SCJ", "TI", scj, scjAdminTI, now, createdUsers);
        createUserIfMissing("admin.qual.scj",   "Admin Qualidade SCJ", "Qualidade",       scj, scjAdminQualidade,   now, createdUsers);
        createUserIfMissing("admin.assist.scj", "Admin Assistencial",  "Gerência Assist", scj, scjAdminAssistencial,now, createdUsers);
        createUserIfMissing("gestor.enf.scj",   "Gerente Enfermagem",  "Enfermagem",      scj, scjGestor,           now, createdUsers);
        createUserIfMissing("sup.cme.scj",      "Supervisora CME",     "CME",             scj, scjOperador,         now, createdUsers);
        createUserIfMissing("enf.cme.scj",      "Enfermeira CME",      "CME",             scj, scjOperador,         now, createdUsers);


        //PERMISSION - Recurso - CME_AUTOCLAVE
        // ===== PERMISSÕES – CME AUTOCLAVE (SCF) =====
        Permission scfAutoclaveRead = findOrCreatePermission(scf,ResourceType.CME_AUTOCLAVE,Action.READ,"LISTA","CME_AUTOCLAVE_READ@LISTA");
        Permission scfAutoclaveCreate = findOrCreatePermission(scf, ResourceType.CME_AUTOCLAVE, Action.CREATE, "FORM", "CME_AUTOCLAVE_CREATE@FORM");
        Permission scfAutoclaveUpdate = findOrCreatePermission(scf, ResourceType.CME_AUTOCLAVE, Action.UPDATE, "FORM", "CME_AUTOCLAVE_UPDATE@FORM");
        Permission scfAutoclaveDelete = findOrCreatePermission(scf, ResourceType.CME_AUTOCLAVE, Action.DELETE, "FORM", "CME_AUTOCLAVE_DELETE@FORM");

        // ===== PERMISSÕES – CME AUTOCLAVE (SCJ) =====
        Permission scjAutoclaveRead = findOrCreatePermission(scj,ResourceType.CME_AUTOCLAVE,Action.READ,"LISTA","CME_AUTOCLAVE_READ@LISTA");
        Permission scjAutoclaveCreate = findOrCreatePermission(scj, ResourceType.CME_AUTOCLAVE, Action.CREATE, "FORM", "CME_AUTOCLAVE_CREATE@FORM");
        Permission scjAutoclaveUpdate = findOrCreatePermission(scj, ResourceType.CME_AUTOCLAVE, Action.UPDATE, "FORM", "CME_AUTOCLAVE_UPDATE@FORM");
        Permission scjAutoclaveDelete = findOrCreatePermission(scj, ResourceType.CME_AUTOCLAVE, Action.DELETE, "FORM", "CME_AUTOCLAVE_DELETE@FORM");

        // ===== ROLE-PERMISSION – SCF - SCJ =====

        // SystemAdmin: tudo
        ensureRolePermission(scfSystemAdmin, scfAutoclaveRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfAutoclaveCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfAutoclaveUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfAutoclaveDelete, scf);

        ensureRolePermission(scjSystemAdmin, scjAutoclaveRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjAutoclaveCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjAutoclaveUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjAutoclaveDelete, scj);

        // Admin TI: tudo (Admin técnico do sistema)
        ensureRolePermission(scfAdminTI, scfAutoclaveRead,   scf);
        ensureRolePermission(scfAdminTI, scfAutoclaveCreate, scf);
        ensureRolePermission(scfAdminTI, scfAutoclaveUpdate, scf);
        ensureRolePermission(scfAdminTI, scfAutoclaveDelete, scf);

        ensureRolePermission(scjAdminTI, scjAutoclaveRead,   scj);
        ensureRolePermission(scjAdminTI, scjAutoclaveCreate, scj);
        ensureRolePermission(scjAdminTI, scjAutoclaveUpdate, scj);
        ensureRolePermission(scjAdminTI, scjAutoclaveDelete, scj);

        // Admin Assistencial: gerencia CME, mas não deleta registro
        ensureRolePermission(scfAdminAssistencial, scfAutoclaveRead,   scf);
        ensureRolePermission(scfAdminAssistencial, scfAutoclaveCreate, scf);
        ensureRolePermission(scfAdminAssistencial, scfAutoclaveUpdate, scf);

        ensureRolePermission(scjAdminAssistencial, scjAutoclaveRead,   scj);
        ensureRolePermission(scjAdminAssistencial, scjAutoclaveCreate, scj);
        ensureRolePermission(scjAdminAssistencial, scjAutoclaveUpdate, scj);

        // Gestor Enfermagem: pode cadastrar/alterar, sem deletar
        ensureRolePermission(scfGestor, scfAutoclaveRead,   scf);
        ensureRolePermission(scfGestor, scfAutoclaveCreate, scf);
        ensureRolePermission(scfGestor, scfAutoclaveUpdate, scf);

        ensureRolePermission(scjGestor, scjAutoclaveRead,   scj);
        ensureRolePermission(scjGestor, scjAutoclaveCreate, scj);
        ensureRolePermission(scjGestor, scjAutoclaveUpdate, scj);

        // Operador (Supervisora + Enfermeira CME, via role OPERADOR)
        // Aqui você controla o escopo via Policy (CME apenas).
        ensureRolePermission(scfOperador, scfAutoclaveRead,   scf);
        ensureRolePermission(scfOperador, scfAutoclaveCreate, scf);
        ensureRolePermission(scfOperador, scfAutoclaveUpdate, scf);

        ensureRolePermission(scjOperador, scjAutoclaveRead,   scj);
        ensureRolePermission(scjOperador, scjAutoclaveCreate, scj);
        ensureRolePermission(scjOperador, scjAutoclaveUpdate, scj);

        // Admin Qualidade: só leitura (para auditoria/processo)
        ensureRolePermission(scfAdminQualidade, scfAutoclaveRead, scf);

        ensureRolePermission(scjAdminQualidade, scjAutoclaveRead, scj);

        // Leitor: só leitura, se quiser
        ensureRolePermission(scfLeitor, scfAutoclaveRead, scf);

        ensureRolePermission(scjLeitor, scjAutoclaveRead, scj);

        // ===== POLICIES / CME AUTOCLAVE =====
        // operador (CME) pode CRUD autoclaves APENAS da CME
        //SCF
        Policy scfCmeAutoclaveOperadorPolicy = buildPolicy(
                scf, Set.of(scfOperador), ResourceType.CME_AUTOCLAVE, Action.READ, "LISTA", Effect.ALLOW,
                20, "Operador pode listar autoclaves da CME");

        scfCmeAutoclaveOperadorPolicy.getConditions()
                .add(new PolicyCondition(null, scfCmeAutoclaveOperadorPolicy,
                        "TARGET_DEPARTMENT", "EQ", "CME"));
        policyRepository.save(scfCmeAutoclaveOperadorPolicy);
        //SCJ
        Policy scjCmeAutoclaveOperadorPolicy = buildPolicy(
                scj, Set.of(scjOperador), ResourceType.CME_AUTOCLAVE, Action.READ, "LISTA", Effect.ALLOW,
                20, "Operador pode listar autoclaves da CME");

        scjCmeAutoclaveOperadorPolicy.getConditions()
                .add(new PolicyCondition(null, scjCmeAutoclaveOperadorPolicy,
                        "TARGET_DEPARTMENT", "EQ", "CME"));
        policyRepository.save(scjCmeAutoclaveOperadorPolicy);

        // Pode replicar para CREATE / UPDATE se você estiver avaliando por request:
        //SCF
        Policy scfCmeAutoclaveOperadorWritePolicy = buildPolicy(
                scf, Set.of(scfOperador), ResourceType.CME_AUTOCLAVE, Action.UPDATE, "FORM",
                Effect.ALLOW, 20, "Operador pode atualizar autoclaves da CME"
        );
        scfCmeAutoclaveOperadorWritePolicy.getConditions().add(
                new PolicyCondition(null, scfCmeAutoclaveOperadorWritePolicy,
                        "TARGET_DEPARTMENT", "EQ", "CME")
        );
        policyRepository.save(scfCmeAutoclaveOperadorWritePolicy);
        //SCJ
        Policy scjCmeAutoclaveOperadorWritePolicy = buildPolicy(
                scj, Set.of(scjOperador), ResourceType.CME_AUTOCLAVE, Action.UPDATE, "FORM",
                Effect.ALLOW, 20, "Operador pode atualizar autoclaves da CME"
        );
        scjCmeAutoclaveOperadorWritePolicy.getConditions().add(
                new PolicyCondition(null, scjCmeAutoclaveOperadorWritePolicy,
                        "TARGET_DEPARTMENT", "EQ", "CME")
        );
        policyRepository.save(scjCmeAutoclaveOperadorWritePolicy);

        // Admin Assistencial: pode tudo de autoclave, sem filtro de setor
        //SCF
        policyRepository.save(buildPolicy(scf, Set.of(scfAdminAssistencial), ResourceType.CME_AUTOCLAVE,
                Action.UPDATE, "FORM", Effect.ALLOW, 10, "Admin assistencial pode alterar qualquer autoclave"
        ));
        //SCJ
        policyRepository.save(buildPolicy(scj, Set.of(scjAdminAssistencial), ResourceType.CME_AUTOCLAVE,
                Action.UPDATE, "FORM", Effect.ALLOW, 10, "Admin assistencial pode alterar qualquer autoclave"
        ));

        // Admin Qualidade: só READ, sem restrição
        //SCF
        policyRepository.save(buildPolicy(
                scf, Set.of(scfAdminQualidade), ResourceType.CME_AUTOCLAVE, Action.READ, "LISTA",
                Effect.ALLOW, 15, "Qualidade pode visualizar autoclaves para auditoria"
        ));
        //SCJ
        policyRepository.save(buildPolicy(
                scj, Set.of(scjAdminQualidade), ResourceType.CME_AUTOCLAVE, Action.READ, "LISTA",
                Effect.ALLOW, 15, "Qualidade pode visualizar autoclaves para auditoria"
        ));

        // Exceção: Enfermeira CME pode deletar autoclave por 2 dias
        userRepository.findByUsernameIgnoreCase("enf.cme.scf").ifPresent(user -> {
            UserPermissionOverride override = new UserPermissionOverride();
            override.setUser(user);
            override.setTenant(scf);
            override.setResource(ResourceType.CME_AUTOCLAVE);
            override.setAction(Action.DELETE);
            override.setFeature("FORM");
            override.setEffect(Effect.ALLOW);
            override.setPriority(5); // prioridade alta, acima das policies padrão
            override.setReason("Liberação temporária para ajuste de cadastro de autoclaves");
            override.setValidFrom(LocalDateTime.now());
            override.setValidUntil(LocalDateTime.now().plusDays(2));
            // targetSetor opcional (se quiser prender à CME)
            // override.setTargetSetor(setorCmeScf);

            userPermissionOverrideRepository.save(override);
        });


        //PAPEIS ORGANIZACIONAIS
        // ===== SETORES (IAM / ORG) =====
        Setor setorCmeScf = setorRepository.save(
                new Setor(null, scf, "Central de Material Esterilização", TipoSetor.CME, "CME")
        );

        Setor setorCmeScj = setorRepository.save(
                new Setor(null, scj, "Central de Material Esterilização", TipoSetor.CME, "CME")
        );

        Setor setorEnfermagemScf = setorRepository.save(
                new Setor(null, scf, "Gerência de Enfermagem", TipoSetor.ENFERMAGEM, "Gerencia Enfermagem")
        );

        Setor setorEnfermagemScj = setorRepository.save(
                new Setor(null, scj, "Gerência de Enfermagem", TipoSetor.ENFERMAGEM, "Gerencia Enfermagem")
        );

        Setor setorQualidadeScf = setorRepository.save(
                new Setor(null, scf, "Gestão da Qualidade", TipoSetor.QUALIDADE, "Qualidade")
        );

        Setor setorQualidadeScj = setorRepository.save(
                new Setor(null, scj, "Gestão da Qualidade", TipoSetor.QUALIDADE, "Qualidade")
        );

        // ===== ORG ROLE ASSIGNMENTS – SCF =====
        LocalDate today = LocalDate.now();
        PeriodoVigencia vigenciaPadrao = new PeriodoVigencia(today.atStartOfDay().toInstant(ZoneOffset.UTC), null);

        // Supervisora CME → GERENCIA_SETOR (CME)
        userRepository.findByUsernameIgnoreCase("sup.cme.scf").ifPresent(user -> {
            OrgRoleAssignment ora = new OrgRoleAssignment();
            ora.setTenant(scf);
            ora.setUser(user);
            ora.setSetor(setorCmeScf);
            ora.setRoleType(OrgRoleType.GERENCIA_SETOR);
            ora.setVigencia(vigenciaPadrao);
            orgRoleAssignmentRepository.save(ora);
        });

        userRepository.findByUsernameIgnoreCase("sup.cme.scj").ifPresent(user -> {
            OrgRoleAssignment ora = new OrgRoleAssignment();
            ora.setTenant(scj);
            ora.setUser(user);
            ora.setSetor(setorCmeScj);
            ora.setRoleType(OrgRoleType.GERENCIA_SETOR);
            ora.setVigencia(vigenciaPadrao);
            orgRoleAssignmentRepository.save(ora);
        });

        // Gerente Enfermagem → ENFERMAGEM_GERENTE
        userRepository.findByUsernameIgnoreCase("gestor.enf.scf").ifPresent(user -> {
            OrgRoleAssignment ora = new OrgRoleAssignment();
            ora.setTenant(scf);
            ora.setUser(user);
            ora.setSetor(setorEnfermagemScf);
            ora.setRoleType(OrgRoleType.ENFERMAGEM_GERENTE);
            ora.setVigencia(vigenciaPadrao);
            orgRoleAssignmentRepository.save(ora);
        });

        userRepository.findByUsernameIgnoreCase("gestor.enf.scj").ifPresent(user -> {
            OrgRoleAssignment ora = new OrgRoleAssignment();
            ora.setTenant(scj);
            ora.setUser(user);
            ora.setSetor(setorEnfermagemScj);
            ora.setRoleType(OrgRoleType.ENFERMAGEM_GERENTE);
            ora.setVigencia(vigenciaPadrao);
            orgRoleAssignmentRepository.save(ora);
        });

        // Gerente Qualidade → QUALIDADE_GERENTE
        userRepository.findByUsernameIgnoreCase("admin.qual.scf").ifPresent(user -> {
            OrgRoleAssignment ora = new OrgRoleAssignment();
            ora.setTenant(scf);
            ora.setUser(user);
            ora.setSetor(setorQualidadeScf);
            ora.setRoleType(OrgRoleType.QUALIDADE_GERENTE);
            ora.setVigencia(vigenciaPadrao);
            orgRoleAssignmentRepository.save(ora);
        });

        userRepository.findByUsernameIgnoreCase("admin.qual.scj").ifPresent(user -> {
            OrgRoleAssignment ora = new OrgRoleAssignment();
            ora.setTenant(scj);
            ora.setUser(user);
            ora.setSetor(setorQualidadeScj);
            ora.setRoleType(OrgRoleType.QUALIDADE_GERENTE);
            ora.setVigencia(vigenciaPadrao);
            orgRoleAssignmentRepository.save(ora);
        });

        // Diretora Assistencial (ex.: usa DIRETOR_TECNICO, se for o caso)
        userRepository.findByUsernameIgnoreCase("admin.assist.scf").ifPresent(user -> {
            OrgRoleAssignment ora = new OrgRoleAssignment();
            ora.setTenant(scf);
            ora.setUser(user);
            ora.setSetor(setorEnfermagemScf); // ou outro setor macro da diretoria
            ora.setRoleType(OrgRoleType.DIRETOR_TECNICO);
            ora.setVigencia(vigenciaPadrao);
            orgRoleAssignmentRepository.save(ora);
        });

        userRepository.findByUsernameIgnoreCase("admin.assist.scj").ifPresent(user -> {
            OrgRoleAssignment ora = new OrgRoleAssignment();
            ora.setTenant(scj);
            ora.setUser(user);
            ora.setSetor(setorEnfermagemScj); // ou outro setor macro da diretoria
            ora.setRoleType(OrgRoleType.DIRETOR_TECNICO);
            ora.setVigencia(vigenciaPadrao);
            orgRoleAssignmentRepository.save(ora);
        });

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
