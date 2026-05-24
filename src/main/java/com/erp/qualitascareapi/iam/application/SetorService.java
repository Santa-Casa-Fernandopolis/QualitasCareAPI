package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.api.dto.SetorDto;
import com.erp.qualitascareapi.iam.api.dto.SetorRequest;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.SetorEspecialidade;
import com.erp.qualitascareapi.iam.domain.SetorTipoCadastro;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import com.erp.qualitascareapi.iam.repo.OrgRoleAssignmentRepository;
import com.erp.qualitascareapi.iam.repo.SetorEspecialidadeRepository;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.SetorTipoRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class SetorService {

    private final SetorRepository setorRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final OrgRoleAssignmentRepository orgRoleAssignmentRepository;
    private final SetorTipoRepository setorTipoRepository;
    private final SetorEspecialidadeRepository setorEspecialidadeRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public SetorService(SetorRepository setorRepository,
                        TenantRepository tenantRepository,
                        UserRepository userRepository,
                        OrgRoleAssignmentRepository orgRoleAssignmentRepository,
                        SetorTipoRepository setorTipoRepository,
                        SetorEspecialidadeRepository setorEspecialidadeRepository,
                        TenantScopeGuard tenantScopeGuard) {
        this.setorRepository = setorRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.orgRoleAssignmentRepository = orgRoleAssignmentRepository;
        this.setorTipoRepository = setorTipoRepository;
        this.setorEspecialidadeRepository = setorEspecialidadeRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<SetorDto> list(Long tenantId, Pageable pageable) {
        Long contextTenantId = tenantScopeGuard.currentTenantId();
        Long effectiveTenantId = contextTenantId != null ? contextTenantId : tenantId;
        Page<Setor> setores = effectiveTenantId != null
                ? setorRepository.findAllByTenantId(effectiveTenantId, pageable)
                : setorRepository.findAll(pageable);
        return setores.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public SetorDto get(Long id) {
        return setorRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Setor", id));
    }

    @Transactional
    public SetorDto create(SetorRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.tenantId()));

        String nomeNormalizado = normalizeName(request.nome());
        validateUniqueName(tenant.getId(), nomeNormalizado, null);

        Setor setor = new Setor();
        setor.setTenant(tenant);
        applyRequest(request, setor, nomeNormalizado);

        return toDto(setorRepository.save(setor));
    }

    @Transactional
    public SetorDto update(Long id, SetorRequest request) {
        Setor setor = setorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Setor", id));

        Long tenantId = request.tenantId();
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));

        String nomeNormalizado = normalizeName(request.nome());
        validateUniqueName(tenant.getId(), nomeNormalizado, setor.getId());

        setor.setTenant(tenant);
        applyRequest(request, setor, nomeNormalizado);

        return toDto(setor);
    }

    @Transactional
    public void delete(Long id) {
        if (!setorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Setor", id);
        }
        setorRepository.deleteById(id);
    }

    private void applyRequest(SetorRequest request, Setor setor, String nomeNormalizado) {
        setor.setNome(nomeNormalizado);
        setor.setTipo(request.tipo());
        setor.setTipoCadastro(resolveTipoCadastro(setor.getTenant().getId(), request.tipoSetorId()));
        setor.setEspecialidade(resolveEspecialidade(setor.getTenant().getId(), request.especialidadeId()));
        setor.setDescricao(StringUtils.hasText(request.descricao()) ? request.descricao().trim() : null);
        setor.setSupervisor(resolveSupervisor(setor.getTenant().getId(), request.supervisorId()));
    }

    private SetorTipoCadastro resolveTipoCadastro(Long tenantId, Long tipoSetorId) {
        if (tipoSetorId == null) {
            return null;
        }
        SetorTipoCadastro tipoCadastro = setorTipoRepository.findById(tipoSetorId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de setor", tipoSetorId));
        validateSameTenant(tenantId, tipoCadastro.getTenant().getId(), "tipoSetorId", tipoSetorId);
        return tipoCadastro;
    }

    private SetorEspecialidade resolveEspecialidade(Long tenantId, Long especialidadeId) {
        if (especialidadeId == null) {
            return null;
        }
        SetorEspecialidade especialidade = setorEspecialidadeRepository.findById(especialidadeId)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidade de setor", especialidadeId));
        validateSameTenant(tenantId, especialidade.getTenant().getId(), "especialidadeId", especialidadeId);
        return especialidade;
    }

    private void validateSameTenant(Long setorTenantId, Long catalogTenantId, String field, Long id) {
        if (!setorTenantId.equals(catalogTenantId)) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST,
                    "setor.catalog.tenant.invalid",
                    "O cadastro selecionado pertence a outro tenant.",
                    Map.of("tenantId", setorTenantId, field, id)
            );
        }
    }

    private User resolveSupervisor(Long tenantId, Long supervisorId) {
        if (supervisorId == null) {
            return null;
        }
        User supervisor = userRepository.findByIdAndTenant_Id(supervisorId, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor", supervisorId));
        boolean isSupervisor = orgRoleAssignmentRepository.existsByTenant_IdAndUser_IdAndRoleTypeAndActiveTrue(
                tenantId, supervisorId, OrgRoleType.GERENCIA_SETOR);
        if (!isSupervisor) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST,
                    "setor.supervisor.invalid",
                    "O usuário selecionado precisa possuir vínculo ativo de Gerência de Setor.",
                    Map.of("supervisorId", supervisorId, "roleType", OrgRoleType.GERENCIA_SETOR.name())
            );
        }
        return supervisor;
    }

    private String normalizeName(String nome) {
        if (!StringUtils.hasText(nome)) {
            return nome;
        }
        return nome.trim();
    }

    private void validateUniqueName(Long tenantId, String nome, Long currentId) {
        setorRepository.findByTenantIdAndNomeIgnoreCase(tenantId, nome)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ApplicationException(
                            HttpStatus.CONFLICT,
                            "setor.duplicated",
                            "Já existe um setor com este nome para o tenant informado.",
                            Map.of(
                                    "tenantId", tenantId,
                                    "nome", nome
                            )
                    );
                });
    }

    private SetorDto toDto(Setor setor) {
        Tenant tenant = setor.getTenant();
        return new SetorDto(
                setor.getId(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getName() : null,
                setor.getNome(),
                setor.getTipo(),
                setor.getTipoCadastro() != null ? setor.getTipoCadastro().getId() : null,
                setor.getTipoCadastro() != null ? setor.getTipoCadastro().getNome() : null,
                setor.getEspecialidade() != null ? setor.getEspecialidade().getId() : null,
                setor.getEspecialidade() != null ? setor.getEspecialidade().getNome() : null,
                setor.getDescricao(),
                setor.getSupervisor() != null ? setor.getSupervisor().getId() : null,
                setor.getSupervisor() != null
                        ? (StringUtils.hasText(setor.getSupervisor().getFullName())
                        ? setor.getSupervisor().getFullName()
                        : setor.getSupervisor().getUsername())
                        : null
        );
    }
}
