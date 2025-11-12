package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.common.vo.PeriodoVigencia;
import com.erp.qualitascareapi.iam.api.dto.OrgRoleAssignmentDto;
import com.erp.qualitascareapi.iam.api.dto.OrgRoleAssignmentRequest;
import com.erp.qualitascareapi.iam.api.dto.PeriodoVigenciaDto;
import com.erp.qualitascareapi.iam.api.dto.PeriodoVigenciaRequest;
import com.erp.qualitascareapi.iam.domain.OrgRoleAssignment;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import com.erp.qualitascareapi.iam.repo.OrgRoleAssignmentRepository;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OrgRoleAssignmentService {

    private final OrgRoleAssignmentRepository orgRoleAssignmentRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SetorRepository setorRepository;

    public OrgRoleAssignmentService(OrgRoleAssignmentRepository orgRoleAssignmentRepository,
                                    TenantRepository tenantRepository,
                                    UserRepository userRepository,
                                    SetorRepository setorRepository) {
        this.orgRoleAssignmentRepository = orgRoleAssignmentRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.setorRepository = setorRepository;
    }

    @Transactional(readOnly = true)
    public Page<OrgRoleAssignmentDto> list(Long tenantId, OrgRoleType roleType, Boolean active, Pageable pageable) {
        Specification<OrgRoleAssignment> spec = Specification.where(null);

        if (tenantId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("tenant").get("id"), tenantId));
        }
        if (roleType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("roleType"), roleType));
        }
        if (active != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("active"), active));
        }

        return orgRoleAssignmentRepository.findAll(spec, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public OrgRoleAssignmentDto get(Long id) {
        return orgRoleAssignmentRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("OrgRoleAssignment", id));
    }

    @Transactional
    public OrgRoleAssignmentDto create(OrgRoleAssignmentRequest request) {
        OrgRoleAssignment assignment = new OrgRoleAssignment();
        applyRequest(assignment, request);
        return toDto(orgRoleAssignmentRepository.save(assignment));
    }

    @Transactional
    public OrgRoleAssignmentDto update(Long id, OrgRoleAssignmentRequest request) {
        OrgRoleAssignment assignment = orgRoleAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("OrgRoleAssignment", id));
        applyRequest(assignment, request);
        return toDto(assignment);
    }

    @Transactional
    public void delete(Long id) {
        if (!orgRoleAssignmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("OrgRoleAssignment", id);
        }
        orgRoleAssignmentRepository.deleteById(id);
    }

    private void applyRequest(OrgRoleAssignment assignment, OrgRoleAssignmentRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BadRequestException("User not found", Map.of("userId", request.userId())));

        if (user.getTenant() == null || !user.getTenant().getId().equals(tenant.getId())) {
            throw new BadRequestException("User does not belong to tenant",
                    Map.of("tenantId", request.tenantId(), "userId", request.userId()));
        }

        Setor setor = null;
        if (request.setorId() != null) {
            setor = setorRepository.findById(request.setorId())
                    .orElseThrow(() -> new BadRequestException("Setor not found", Map.of("setorId", request.setorId())));
            if (setor.getTenant() == null || !setor.getTenant().getId().equals(tenant.getId())) {
                throw new BadRequestException("Setor does not belong to tenant",
                        Map.of("tenantId", request.tenantId(), "setorId", request.setorId()));
            }
        }

        assignment.setTenant(tenant);
        assignment.setUser(user);
        assignment.setRoleType(request.roleType());
        assignment.setSetor(setor);
        assignment.setActive(request.active() == null ? Boolean.TRUE : request.active());
        applyVigencia(assignment, request.vigencia());
    }

    private void applyVigencia(OrgRoleAssignment assignment, PeriodoVigenciaRequest vigenciaRequest) {
        if (vigenciaRequest == null || (vigenciaRequest.inicio() == null && vigenciaRequest.fim() == null)) {
            assignment.setVigencia(null);
            return;
        }
        assignment.setVigencia(new PeriodoVigencia(vigenciaRequest.inicio(), vigenciaRequest.fim()));
    }

    private OrgRoleAssignmentDto toDto(OrgRoleAssignment assignment) {
        Tenant tenant = assignment.getTenant();
        User user = assignment.getUser();
        Setor setor = assignment.getSetor();
        PeriodoVigencia vigencia = assignment.getVigencia();
        return new OrgRoleAssignmentDto(
                assignment.getId(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getCode() : null,
                tenant != null ? tenant.getName() : null,
                assignment.getRoleType(),
                user != null ? user.getId() : null,
                user != null ? user.getUsername() : null,
                user != null ? user.getFullName() : null,
                setor != null ? setor.getId() : null,
                setor != null ? setor.getNome() : null,
                setor != null ? setor.getTipo() : null,
                toVigenciaDto(vigencia),
                assignment.getActive()
        );
    }

    private PeriodoVigenciaDto toVigenciaDto(PeriodoVigencia vigencia) {
        if (vigencia == null) {
            return null;
        }
        return new PeriodoVigenciaDto(vigencia.getInicio(), vigencia.getFim());
    }
}
