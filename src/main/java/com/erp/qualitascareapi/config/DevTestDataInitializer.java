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

        // ===== PERMISSÕES BASE – IAM / SECURITY (SCF) =====
        // IAM_TENANT – gestão de tenants
        Permission scfIamTenantRead = findOrCreatePermission(
                scf, ResourceType.IAM_TENANT, Action.READ, "LISTA", "IAM_TENANT_READ@LISTA");
        Permission scfIamTenantCreate = findOrCreatePermission(
                scf, ResourceType.IAM_TENANT, Action.CREATE, "FORM", "IAM_TENANT_CREATE@FORM");
        Permission scfIamTenantUpdate = findOrCreatePermission(
                scf, ResourceType.IAM_TENANT, Action.UPDATE, "FORM", "IAM_TENANT_UPDATE@FORM");
        Permission scfIamTenantDelete = findOrCreatePermission(
                scf, ResourceType.IAM_TENANT, Action.DELETE, "FORM", "IAM_TENANT_DELETE@FORM");

        // IAM_USER – gestão de usuários
        Permission scfIamUserRead = findOrCreatePermission(
                scf, ResourceType.IAM_USER, Action.READ, "LISTA", "IAM_USER_READ@LISTA");
        Permission scfIamUserCreate = findOrCreatePermission(
                scf, ResourceType.IAM_USER, Action.CREATE, "FORM", "IAM_USER_CREATE@FORM");
        Permission scfIamUserUpdate = findOrCreatePermission(
                scf, ResourceType.IAM_USER, Action.UPDATE, "FORM", "IAM_USER_UPDATE@FORM");
        Permission scfIamUserDelete = findOrCreatePermission(
                scf, ResourceType.IAM_USER, Action.DELETE, "FORM", "IAM_USER_DELETE@FORM");

        // IAM_SETOR – gestão de setores organizacionais
        Permission scfIamSetorRead = findOrCreatePermission(
                scf, ResourceType.IAM_SETOR, Action.READ, "LISTA", "IAM_SETOR_READ@LISTA");
        Permission scfIamSetorCreate = findOrCreatePermission(
                scf, ResourceType.IAM_SETOR, Action.CREATE, "FORM", "IAM_SETOR_CREATE@FORM");
        Permission scfIamSetorUpdate = findOrCreatePermission(
                scf, ResourceType.IAM_SETOR, Action.UPDATE, "FORM", "IAM_SETOR_UPDATE@FORM");
        Permission scfIamSetorDelete = findOrCreatePermission(
                scf, ResourceType.IAM_SETOR, Action.DELETE, "FORM", "IAM_SETOR_DELETE@FORM");

        // IAM_ORG_ROLE_ASSIGNMENT – quem é gerente / supervisor / diretor
        Permission scfIamOrgRoleAssignRead = findOrCreatePermission(
                scf, ResourceType.IAM_ORG_ROLE_ASSIGNMENT, Action.READ, "LISTA", "IAM_ORG_ROLE_ASSIGN_READ@LISTA");
        Permission scfIamOrgRoleAssignCreate = findOrCreatePermission(
                scf, ResourceType.IAM_ORG_ROLE_ASSIGNMENT, Action.CREATE, "FORM", "IAM_ORG_ROLE_ASSIGN_CREATE@FORM");
        Permission scfIamOrgRoleAssignUpdate = findOrCreatePermission(
                scf, ResourceType.IAM_ORG_ROLE_ASSIGNMENT, Action.UPDATE, "FORM", "IAM_ORG_ROLE_ASSIGN_UPDATE@FORM");
        Permission scfIamOrgRoleAssignDelete = findOrCreatePermission(
                scf, ResourceType.IAM_ORG_ROLE_ASSIGNMENT, Action.DELETE, "FORM", "IAM_ORG_ROLE_ASSIGN_DELETE@FORM");

        // SECURITY_ROLE – cadastro de roles técnicas
        Permission scfSecRoleRead = findOrCreatePermission(
                scf, ResourceType.SECURITY_ROLE, Action.READ, "LISTA", "SEC_ROLE_READ@LISTA");
        Permission scfSecRoleCreate = findOrCreatePermission(
                scf, ResourceType.SECURITY_ROLE, Action.CREATE, "FORM", "SEC_ROLE_CREATE@FORM");
        Permission scfSecRoleUpdate = findOrCreatePermission(
                scf, ResourceType.SECURITY_ROLE, Action.UPDATE, "FORM", "SEC_ROLE_UPDATE@FORM");
        Permission scfSecRoleDelete = findOrCreatePermission(
                scf, ResourceType.SECURITY_ROLE, Action.DELETE, "FORM", "SEC_ROLE_DELETE@FORM");

        // SECURITY_PERMISSION – catálogo de permissões
        Permission scfSecPermRead = findOrCreatePermission(
                scf, ResourceType.SECURITY_PERMISSION, Action.READ, "LISTA", "SEC_PERMISSION_READ@LISTA");
        Permission scfSecPermCreate = findOrCreatePermission(
                scf, ResourceType.SECURITY_PERMISSION, Action.CREATE, "FORM", "SEC_PERMISSION_CREATE@FORM");
        Permission scfSecPermUpdate = findOrCreatePermission(
                scf, ResourceType.SECURITY_PERMISSION, Action.UPDATE, "FORM", "SEC_PERMISSION_UPDATE@FORM");
        Permission scfSecPermDelete = findOrCreatePermission(
                scf, ResourceType.SECURITY_PERMISSION, Action.DELETE, "FORM", "SEC_PERMISSION_DELETE@FORM");

        // SECURITY_ROLE_PERMISSION – vincular role ↔ permission
        Permission scfSecRolePermRead = findOrCreatePermission(
                scf, ResourceType.SECURITY_ROLE_PERMISSION, Action.READ, "LISTA", "SEC_ROLE_PERMISSION_READ@LISTA");
        Permission scfSecRolePermCreate = findOrCreatePermission(
                scf, ResourceType.SECURITY_ROLE_PERMISSION, Action.CREATE, "FORM", "SEC_ROLE_PERMISSION_CREATE@FORM");
        Permission scfSecRolePermUpdate = findOrCreatePermission(
                scf, ResourceType.SECURITY_ROLE_PERMISSION, Action.UPDATE, "FORM", "SEC_ROLE_PERMISSION_UPDATE@FORM");
        Permission scfSecRolePermDelete = findOrCreatePermission(
                scf, ResourceType.SECURITY_ROLE_PERMISSION, Action.DELETE, "FORM", "SEC_ROLE_PERMISSION_DELETE@FORM");

        // SECURITY_POLICY – regras ABAC (negócio + técnico)
        Permission scfSecPolicyRead = findOrCreatePermission(
                scf, ResourceType.SECURITY_POLICY, Action.READ, "LISTA", "SEC_POLICY_READ@LISTA");
        Permission scfSecPolicyCreate = findOrCreatePermission(
                scf, ResourceType.SECURITY_POLICY, Action.CREATE, "FORM", "SEC_POLICY_CREATE@FORM");
        Permission scfSecPolicyUpdate = findOrCreatePermission(
                scf, ResourceType.SECURITY_POLICY, Action.UPDATE, "FORM", "SEC_POLICY_UPDATE@FORM");
        Permission scfSecPolicyDelete = findOrCreatePermission(
                scf, ResourceType.SECURITY_POLICY, Action.DELETE, "FORM", "SEC_POLICY_DELETE@FORM");

        // SECURITY_USER_PERMISSION_OVERRIDE – exceções
        Permission scfSecUserOverrideRead = findOrCreatePermission(
                scf, ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, Action.READ, "LISTA", "SEC_USER_OVERRIDE_READ@LISTA");
        Permission scfSecUserOverrideCreate = findOrCreatePermission(
                scf, ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, Action.CREATE, "FORM", "SEC_USER_OVERRIDE_CREATE@FORM");
        Permission scfSecUserOverrideUpdate = findOrCreatePermission(
                scf, ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, Action.UPDATE, "FORM", "SEC_USER_OVERRIDE_UPDATE@FORM");
        Permission scfSecUserOverrideDelete = findOrCreatePermission(
                scf, ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, Action.DELETE, "FORM", "SEC_USER_OVERRIDE_DELETE@FORM");

        // ===== PERMISSÕES BASE – IAM / SECURITY (SCJ) =====
        Permission scjIamTenantRead = findOrCreatePermission(
                scj, ResourceType.IAM_TENANT, Action.READ, "LISTA", "IAM_TENANT_READ@LISTA");
        Permission scjIamTenantCreate = findOrCreatePermission(
                scj, ResourceType.IAM_TENANT, Action.CREATE, "FORM", "IAM_TENANT_CREATE@FORM");
        Permission scjIamTenantUpdate = findOrCreatePermission(
                scj, ResourceType.IAM_TENANT, Action.UPDATE, "FORM", "IAM_TENANT_UPDATE@FORM");
        Permission scjIamTenantDelete = findOrCreatePermission(
                scj, ResourceType.IAM_TENANT, Action.DELETE, "FORM", "IAM_TENANT_DELETE@FORM");

        Permission scjIamUserRead = findOrCreatePermission(
                scj, ResourceType.IAM_USER, Action.READ, "LISTA", "IAM_USER_READ@LISTA");
        Permission scjIamUserCreate = findOrCreatePermission(
                scj, ResourceType.IAM_USER, Action.CREATE, "FORM", "IAM_USER_CREATE@FORM");
        Permission scjIamUserUpdate = findOrCreatePermission(
                scj, ResourceType.IAM_USER, Action.UPDATE, "FORM", "IAM_USER_UPDATE@FORM");
        Permission scjIamUserDelete = findOrCreatePermission(
                scj, ResourceType.IAM_USER, Action.DELETE, "FORM", "IAM_USER_DELETE@FORM");

        Permission scjIamSetorRead = findOrCreatePermission(
                scj, ResourceType.IAM_SETOR, Action.READ, "LISTA", "IAM_SETOR_READ@LISTA");
        Permission scjIamSetorCreate = findOrCreatePermission(
                scj, ResourceType.IAM_SETOR, Action.CREATE, "FORM", "IAM_SETOR_CREATE@FORM");
        Permission scjIamSetorUpdate = findOrCreatePermission(
                scj, ResourceType.IAM_SETOR, Action.UPDATE, "FORM", "IAM_SETOR_UPDATE@FORM");
        Permission scjIamSetorDelete = findOrCreatePermission(
                scj, ResourceType.IAM_SETOR, Action.DELETE, "FORM", "IAM_SETOR_DELETE@FORM");

        Permission scjIamOrgRoleAssignRead = findOrCreatePermission(
                scj, ResourceType.IAM_ORG_ROLE_ASSIGNMENT, Action.READ, "LISTA", "IAM_ORG_ROLE_ASSIGN_READ@LISTA");
        Permission scjIamOrgRoleAssignCreate = findOrCreatePermission(
                scj, ResourceType.IAM_ORG_ROLE_ASSIGNMENT, Action.CREATE, "FORM", "IAM_ORG_ROLE_ASSIGN_CREATE@FORM");
        Permission scjIamOrgRoleAssignUpdate = findOrCreatePermission(
                scj, ResourceType.IAM_ORG_ROLE_ASSIGNMENT, Action.UPDATE, "FORM", "IAM_ORG_ROLE_ASSIGN_UPDATE@FORM");
        Permission scjIamOrgRoleAssignDelete = findOrCreatePermission(
                scj, ResourceType.IAM_ORG_ROLE_ASSIGNMENT, Action.DELETE, "FORM", "IAM_ORG_ROLE_ASSIGN_DELETE@FORM");

        Permission scjSecRoleRead = findOrCreatePermission(
                scj, ResourceType.SECURITY_ROLE, Action.READ, "LISTA", "SEC_ROLE_READ@LISTA");
        Permission scjSecRoleCreate = findOrCreatePermission(
                scj, ResourceType.SECURITY_ROLE, Action.CREATE, "FORM", "SEC_ROLE_CREATE@FORM");
        Permission scjSecRoleUpdate = findOrCreatePermission(
                scj, ResourceType.SECURITY_ROLE, Action.UPDATE, "FORM", "SEC_ROLE_UPDATE@FORM");
        Permission scjSecRoleDelete = findOrCreatePermission(
                scj, ResourceType.SECURITY_ROLE, Action.DELETE, "FORM", "SEC_ROLE_DELETE@FORM");

        Permission scjSecPermRead = findOrCreatePermission(
                scj, ResourceType.SECURITY_PERMISSION, Action.READ, "LISTA", "SEC_PERMISSION_READ@LISTA");
        Permission scjSecPermCreate = findOrCreatePermission(
                scj, ResourceType.SECURITY_PERMISSION, Action.CREATE, "FORM", "SEC_PERMISSION_CREATE@FORM");
        Permission scjSecPermUpdate = findOrCreatePermission(
                scj, ResourceType.SECURITY_PERMISSION, Action.UPDATE, "FORM", "SEC_PERMISSION_UPDATE@FORM");
        Permission scjSecPermDelete = findOrCreatePermission(
                scj, ResourceType.SECURITY_PERMISSION, Action.DELETE, "FORM", "SEC_PERMISSION_DELETE@FORM");

        Permission scjSecRolePermRead = findOrCreatePermission(
                scj, ResourceType.SECURITY_ROLE_PERMISSION, Action.READ, "LISTA", "SEC_ROLE_PERMISSION_READ@LISTA");
        Permission scjSecRolePermCreate = findOrCreatePermission(
                scj, ResourceType.SECURITY_ROLE_PERMISSION, Action.CREATE, "FORM", "SEC_ROLE_PERMISSION_CREATE@FORM");
        Permission scjSecRolePermUpdate = findOrCreatePermission(
                scj, ResourceType.SECURITY_ROLE_PERMISSION, Action.UPDATE, "FORM", "SEC_ROLE_PERMISSION_UPDATE@FORM");
        Permission scjSecRolePermDelete = findOrCreatePermission(
                scj, ResourceType.SECURITY_ROLE_PERMISSION, Action.DELETE, "FORM", "SEC_ROLE_PERMISSION_DELETE@FORM");

        Permission scjSecPolicyRead = findOrCreatePermission(
                scj, ResourceType.SECURITY_POLICY, Action.READ, "LISTA", "SEC_POLICY_READ@LISTA");
        Permission scjSecPolicyCreate = findOrCreatePermission(
                scj, ResourceType.SECURITY_POLICY, Action.CREATE, "FORM", "SEC_POLICY_CREATE@FORM");
        Permission scjSecPolicyUpdate = findOrCreatePermission(
                scj, ResourceType.SECURITY_POLICY, Action.UPDATE, "FORM", "SEC_POLICY_UPDATE@FORM");
        Permission scjSecPolicyDelete = findOrCreatePermission(
                scj, ResourceType.SECURITY_POLICY, Action.DELETE, "FORM", "SEC_POLICY_DELETE@FORM");

        Permission scjSecUserOverrideRead = findOrCreatePermission(
                scj, ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, Action.READ, "LISTA", "SEC_USER_OVERRIDE_READ@LISTA");
        Permission scjSecUserOverrideCreate = findOrCreatePermission(
                scj, ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, Action.CREATE, "FORM", "SEC_USER_OVERRIDE_CREATE@FORM");
        Permission scjSecUserOverrideUpdate = findOrCreatePermission(
                scj, ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, Action.UPDATE, "FORM", "SEC_USER_OVERRIDE_UPDATE@FORM");
        Permission scjSecUserOverrideDelete = findOrCreatePermission(
                scj, ResourceType.SECURITY_USER_PERMISSION_OVERRIDE, Action.DELETE, "FORM", "SEC_USER_OVERRIDE_DELETE@FORM");


        // ===== ROLE-PERMISSION – IAM / SECURITY (SCF) =====

        // SYSTEM_ADMIN (SCF) – acesso total IAM / SECURITY
        ensureRolePermission(scfSystemAdmin, scfIamTenantRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfIamTenantCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfIamTenantUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfIamTenantDelete, scf);

        ensureRolePermission(scfSystemAdmin, scfIamUserRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfIamUserCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfIamUserUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfIamUserDelete, scf);

        ensureRolePermission(scfSystemAdmin, scfIamSetorRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfIamSetorCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfIamSetorUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfIamSetorDelete, scf);

        ensureRolePermission(scfSystemAdmin, scfIamOrgRoleAssignRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfIamOrgRoleAssignCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfIamOrgRoleAssignUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfIamOrgRoleAssignDelete, scf);

        ensureRolePermission(scfSystemAdmin, scfSecRoleRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfSecRoleCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecRoleUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecRoleDelete, scf);

        ensureRolePermission(scfSystemAdmin, scfSecPermRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfSecPermCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecPermUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecPermDelete, scf);

        ensureRolePermission(scfSystemAdmin, scfSecRolePermRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfSecRolePermCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecRolePermUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecRolePermDelete, scf);

        ensureRolePermission(scfSystemAdmin, scfSecPolicyRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfSecPolicyCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecPolicyUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecPolicyDelete, scf);

        ensureRolePermission(scfSystemAdmin, scfSecUserOverrideRead,   scf);
        ensureRolePermission(scfSystemAdmin, scfSecUserOverrideCreate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecUserOverrideUpdate, scf);
        ensureRolePermission(scfSystemAdmin, scfSecUserOverrideDelete, scf);


        // ADMIN_TI (SCF) – quase tudo, com restrições em tenant / org-role / override

        // Tenants: só leitura
        ensureRolePermission(scfAdminTI, scfIamTenantRead, scf);

        // Users: CRUD sem delete (mais seguro)
        ensureRolePermission(scfAdminTI, scfIamUserRead,   scf);
        ensureRolePermission(scfAdminTI, scfIamUserCreate, scf);
        ensureRolePermission(scfAdminTI, scfIamUserUpdate, scf);
        // (delete só via SYSTEM_ADMIN)

        // Setores: pode gerenciar estruturalmente
        ensureRolePermission(scfAdminTI, scfIamSetorRead,   scf);
        ensureRolePermission(scfAdminTI, scfIamSetorCreate, scf);
        ensureRolePermission(scfAdminTI, scfIamSetorUpdate, scf);
        // opcional: se quiser permitir delete, acrescentar a linha abaixo
        // ensureRolePermission(scfAdminTI, scfIamSetorDelete, scf);

        // OrgRoleAssignment: só leitura (governança assistencial)
        ensureRolePermission(scfAdminTI, scfIamOrgRoleAssignRead, scf);

        // Roles / Permissions / RolePermission / Policies: pode configurar tecnicamente
        ensureRolePermission(scfAdminTI, scfSecRoleRead,   scf);
        ensureRolePermission(scfAdminTI, scfSecRoleCreate, scf);
        ensureRolePermission(scfAdminTI, scfSecRoleUpdate, scf);

        ensureRolePermission(scfAdminTI, scfSecPermRead,   scf);
        ensureRolePermission(scfAdminTI, scfSecPermCreate, scf);
        ensureRolePermission(scfAdminTI, scfSecPermUpdate, scf);

        ensureRolePermission(scfAdminTI, scfSecRolePermRead,   scf);
        ensureRolePermission(scfAdminTI, scfSecRolePermCreate, scf);
        ensureRolePermission(scfAdminTI, scfSecRolePermUpdate, scf);

        ensureRolePermission(scfAdminTI, scfSecPolicyRead,   scf);
        ensureRolePermission(scfAdminTI, scfSecPolicyCreate, scf);
        ensureRolePermission(scfAdminTI, scfSecPolicyUpdate, scf);
        // delete de policy deixamos só para SYSTEM_ADMIN

        // UserPermissionOverride: só leitura (overrides sensíveis)
        ensureRolePermission(scfAdminTI, scfSecUserOverrideRead, scf);

        // ===== ROLE-PERMISSION – IAM / SECURITY (SCJ) =====
        // SYSTEM_ADMIN (SCJ)
        ensureRolePermission(scjSystemAdmin, scjIamTenantRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjIamTenantCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjIamTenantUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjIamTenantDelete, scj);

        ensureRolePermission(scjSystemAdmin, scjIamUserRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjIamUserCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjIamUserUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjIamUserDelete, scj);

        ensureRolePermission(scjSystemAdmin, scjIamSetorRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjIamSetorCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjIamSetorUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjIamSetorDelete, scj);

        ensureRolePermission(scjSystemAdmin, scjIamOrgRoleAssignRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjIamOrgRoleAssignCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjIamOrgRoleAssignUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjIamOrgRoleAssignDelete, scj);

        ensureRolePermission(scjSystemAdmin, scjSecRoleRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjSecRoleCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecRoleUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecRoleDelete, scj);

        ensureRolePermission(scjSystemAdmin, scjSecPermRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjSecPermCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecPermUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecPermDelete, scj);

        ensureRolePermission(scjSystemAdmin, scjSecRolePermRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjSecRolePermCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecRolePermUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecRolePermDelete, scj);

        ensureRolePermission(scjSystemAdmin, scjSecPolicyRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjSecPolicyCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecPolicyUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecPolicyDelete, scj);

        ensureRolePermission(scjSystemAdmin, scjSecUserOverrideRead,   scj);
        ensureRolePermission(scjSystemAdmin, scjSecUserOverrideCreate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecUserOverrideUpdate, scj);
        ensureRolePermission(scjSystemAdmin, scjSecUserOverrideDelete, scj);

        // ADMIN_TI (SCJ)
        ensureRolePermission(scjAdminTI, scjIamTenantRead, scj);

        ensureRolePermission(scjAdminTI, scjIamUserRead,   scj);
        ensureRolePermission(scjAdminTI, scjIamUserCreate, scj);
        ensureRolePermission(scjAdminTI, scjIamUserUpdate, scj);

        ensureRolePermission(scjAdminTI, scjIamSetorRead,   scj);
        ensureRolePermission(scjAdminTI, scjIamSetorCreate, scj);
        ensureRolePermission(scjAdminTI, scjIamSetorUpdate, scj);
        // opcional: ensureRolePermission(scjAdminTI, scjIamSetorDelete, scj);

        ensureRolePermission(scjAdminTI, scjIamOrgRoleAssignRead, scj);

        ensureRolePermission(scjAdminTI, scjSecRoleRead,   scj);
        ensureRolePermission(scjAdminTI, scjSecRoleCreate, scj);
        ensureRolePermission(scjAdminTI, scjSecRoleUpdate, scj);

        ensureRolePermission(scjAdminTI, scjSecPermRead,   scj);
        ensureRolePermission(scjAdminTI, scjSecPermCreate, scj);
        ensureRolePermission(scjAdminTI, scjSecPermUpdate, scj);

        ensureRolePermission(scjAdminTI, scjSecRolePermRead,   scj);
        ensureRolePermission(scjAdminTI, scjSecRolePermCreate, scj);
        ensureRolePermission(scjAdminTI, scjSecRolePermUpdate, scj);

        ensureRolePermission(scjAdminTI, scjSecPolicyRead,   scj);
        ensureRolePermission(scjAdminTI, scjSecPolicyCreate, scj);
        ensureRolePermission(scjAdminTI, scjSecPolicyUpdate, scj);

        ensureRolePermission(scjAdminTI, scjSecUserOverrideRead, scj);

        //=========================== AUTOCLAVE ==========================================================
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

        
        //=========================== PAPEIS ORGANIZACIONAIS =========================================================
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
