package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.api.dto.SetorDto;
import com.erp.qualitascareapi.iam.api.dto.SetorRequest;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
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

    public SetorService(SetorRepository setorRepository, TenantRepository tenantRepository) {
        this.setorRepository = setorRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional(readOnly = true)
    public Page<SetorDto> list(Long tenantId, Pageable pageable) {
        Page<Setor> setores = tenantId != null
                ? setorRepository.findAllByTenantId(tenantId, pageable)
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
        setor.setDescricao(StringUtils.hasText(request.descricao()) ? request.descricao().trim() : null);
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
                setor.getDescricao()
        );
    }
}
