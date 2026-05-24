package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.api.dto.SetorEspecialidadeDto;
import com.erp.qualitascareapi.iam.api.dto.SetorEspecialidadeRequest;
import com.erp.qualitascareapi.iam.api.dto.SetorTipoDto;
import com.erp.qualitascareapi.iam.api.dto.SetorTipoRequest;
import com.erp.qualitascareapi.iam.domain.SetorEspecialidade;
import com.erp.qualitascareapi.iam.domain.SetorTipoCadastro;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.SetorEspecialidadeRepository;
import com.erp.qualitascareapi.iam.repo.SetorTipoRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
public class SetorCatalogoService {

    private final SetorTipoRepository setorTipoRepository;
    private final SetorEspecialidadeRepository setorEspecialidadeRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public SetorCatalogoService(SetorTipoRepository setorTipoRepository,
                                SetorEspecialidadeRepository setorEspecialidadeRepository,
                                TenantRepository tenantRepository,
                                TenantScopeGuard tenantScopeGuard) {
        this.setorTipoRepository = setorTipoRepository;
        this.setorEspecialidadeRepository = setorEspecialidadeRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<SetorTipoDto> listTipos(Long tenantId, Pageable pageable) {
        Long effectiveTenantId = effectiveTenantId(tenantId);
        Page<SetorTipoCadastro> page = effectiveTenantId != null
                ? setorTipoRepository.findAllByTenantId(effectiveTenantId, pageable)
                : setorTipoRepository.findAll(pageable);
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<SetorTipoDto> listTiposAtivos(Long tenantId) {
        Long effectiveTenantId = effectiveTenantId(tenantId);
        if (effectiveTenantId == null) {
            return setorTipoRepository.findAll().stream().filter(SetorTipoCadastro::isActive).map(this::toDto).toList();
        }
        return setorTipoRepository.findAllByTenantIdAndActiveTrueOrderByNomeAsc(effectiveTenantId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SetorTipoDto createTipo(SetorTipoRequest request) {
        Tenant tenant = resolveTenant(request.tenantId());
        String nome = normalizeName(request.nome());
        validateTipoUnique(tenant.getId(), nome, null);
        SetorTipoCadastro tipo = new SetorTipoCadastro();
        tipo.setTenant(tenant);
        applyTipoRequest(tipo, request, nome);
        return toDto(setorTipoRepository.save(tipo));
    }

    @Transactional
    public SetorTipoDto updateTipo(Long id, SetorTipoRequest request) {
        SetorTipoCadastro tipo = setorTipoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de setor", id));
        Tenant tenant = resolveTenant(request.tenantId());
        String nome = normalizeName(request.nome());
        validateTipoUnique(tenant.getId(), nome, tipo.getId());
        tipo.setTenant(tenant);
        applyTipoRequest(tipo, request, nome);
        return toDto(tipo);
    }

    @Transactional
    public void deleteTipo(Long id) {
        if (!setorTipoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tipo de setor", id);
        }
        setorTipoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<SetorEspecialidadeDto> listEspecialidades(Long tenantId, Pageable pageable) {
        Long effectiveTenantId = effectiveTenantId(tenantId);
        Page<SetorEspecialidade> page = effectiveTenantId != null
                ? setorEspecialidadeRepository.findAllByTenantId(effectiveTenantId, pageable)
                : setorEspecialidadeRepository.findAll(pageable);
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public List<SetorEspecialidadeDto> listEspecialidadesAtivas(Long tenantId) {
        Long effectiveTenantId = effectiveTenantId(tenantId);
        if (effectiveTenantId == null) {
            return setorEspecialidadeRepository.findAll().stream().filter(SetorEspecialidade::isActive).map(this::toDto).toList();
        }
        return setorEspecialidadeRepository.findAllByTenantIdAndActiveTrueOrderByNomeAsc(effectiveTenantId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public SetorEspecialidadeDto createEspecialidade(SetorEspecialidadeRequest request) {
        Tenant tenant = resolveTenant(request.tenantId());
        String nome = normalizeName(request.nome());
        validateEspecialidadeUnique(tenant.getId(), nome, null);
        SetorEspecialidade especialidade = new SetorEspecialidade();
        especialidade.setTenant(tenant);
        applyEspecialidadeRequest(especialidade, request, nome);
        return toDto(setorEspecialidadeRepository.save(especialidade));
    }

    @Transactional
    public SetorEspecialidadeDto updateEspecialidade(Long id, SetorEspecialidadeRequest request) {
        SetorEspecialidade especialidade = setorEspecialidadeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Especialidade de setor", id));
        Tenant tenant = resolveTenant(request.tenantId());
        String nome = normalizeName(request.nome());
        validateEspecialidadeUnique(tenant.getId(), nome, especialidade.getId());
        especialidade.setTenant(tenant);
        applyEspecialidadeRequest(especialidade, request, nome);
        return toDto(especialidade);
    }

    @Transactional
    public void deleteEspecialidade(Long id) {
        if (!setorEspecialidadeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Especialidade de setor", id);
        }
        setorEspecialidadeRepository.deleteById(id);
    }

    private Long effectiveTenantId(Long tenantId) {
        Long contextTenantId = tenantScopeGuard.currentTenantId();
        return contextTenantId != null ? contextTenantId : tenantId;
    }

    private Tenant resolveTenant(Long tenantId) {
        tenantScopeGuard.checkRequestedTenant(tenantId);
        return tenantRepository.findById(tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", tenantId));
    }

    private void applyTipoRequest(SetorTipoCadastro tipo, SetorTipoRequest request, String nome) {
        tipo.setNome(nome);
        tipo.setDescricao(StringUtils.hasText(request.descricao()) ? request.descricao().trim() : null);
        tipo.setActive(request.active() == null || request.active());
    }

    private void applyEspecialidadeRequest(SetorEspecialidade especialidade, SetorEspecialidadeRequest request, String nome) {
        especialidade.setNome(nome);
        especialidade.setDescricao(StringUtils.hasText(request.descricao()) ? request.descricao().trim() : null);
        especialidade.setActive(request.active() == null || request.active());
        especialidade.setTipoSetor(resolveTipoSetor(especialidade.getTenant().getId(), request.tipoSetorId()));
    }

    private SetorTipoCadastro resolveTipoSetor(Long tenantId, Long tipoSetorId) {
        if (tipoSetorId == null) {
            return null;
        }
        SetorTipoCadastro tipo = setorTipoRepository.findById(tipoSetorId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de setor", tipoSetorId));
        if (!tenantId.equals(tipo.getTenant().getId())) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST,
                    "setor.especialidade.tipo.tenant.invalid",
                    "O tipo de setor selecionado pertence a outro tenant.",
                    Map.of("tenantId", tenantId, "tipoSetorId", tipoSetorId)
            );
        }
        return tipo;
    }

    private String normalizeName(String nome) {
        return StringUtils.hasText(nome) ? nome.trim() : nome;
    }

    private void validateTipoUnique(Long tenantId, String nome, Long currentId) {
        setorTipoRepository.findByTenantIdAndNomeIgnoreCase(tenantId, nome)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw duplicated("setor.tipo.duplicated", "Já existe um tipo de setor com este nome para o tenant informado.", tenantId, nome);
                });
    }

    private void validateEspecialidadeUnique(Long tenantId, String nome, Long currentId) {
        setorEspecialidadeRepository.findByTenantIdAndNomeIgnoreCase(tenantId, nome)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw duplicated("setor.especialidade.duplicated", "Já existe uma especialidade de setor com este nome para o tenant informado.", tenantId, nome);
                });
    }

    private ApplicationException duplicated(String code, String message, Long tenantId, String nome) {
        return new ApplicationException(HttpStatus.CONFLICT, code, message, Map.of("tenantId", tenantId, "nome", nome));
    }

    private SetorTipoDto toDto(SetorTipoCadastro tipo) {
        Tenant tenant = tipo.getTenant();
        return new SetorTipoDto(
                tipo.getId(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getName() : null,
                tipo.getNome(),
                tipo.getDescricao(),
                tipo.isActive()
        );
    }

    private SetorEspecialidadeDto toDto(SetorEspecialidade especialidade) {
        Tenant tenant = especialidade.getTenant();
        SetorTipoCadastro tipo = especialidade.getTipoSetor();
        return new SetorEspecialidadeDto(
                especialidade.getId(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getName() : null,
                tipo != null ? tipo.getId() : null,
                tipo != null ? tipo.getNome() : null,
                especialidade.getNome(),
                especialidade.getDescricao(),
                especialidade.isActive()
        );
    }
}

