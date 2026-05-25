package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.CmeEquipamentoDto;
import com.erp.qualitascareapi.cme.api.dto.CmeEquipamentoRequest;
import com.erp.qualitascareapi.cme.domain.CmeEquipamento;
import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import com.erp.qualitascareapi.cme.repo.CmeEquipamentoRepository;
import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@Transactional
public class CmeEquipamentoService {
    private final CmeEquipamentoRepository equipamentoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public CmeEquipamentoService(CmeEquipamentoRepository equipamentoRepository,
                                 TenantRepository tenantRepository,
                                 TenantScopeGuard tenantScopeGuard) {
        this.equipamentoRepository = equipamentoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public List<CmeEquipamentoDto> list() {
        return equipamentoRepository.findAllByTenantIdOrderByNomeAsc(tenantScopeGuard.currentTenantId())
                .stream().map(this::toDto).toList();
    }

    public CmeEquipamentoDto create(CmeEquipamentoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        String codigo = normalizeCodigo(request.codigo());
        if (equipamentoRepository.existsByTenantIdAndCodigoIgnoreCase(tenant.getId(), codigo)) {
            throw new ApplicationException(HttpStatus.CONFLICT, "cme.equipamento.codigo-duplicado",
                    "Já existe um equipamento cadastrado com este código.");
        }
        CmeEquipamento equipamento = new CmeEquipamento();
        equipamento.setTenant(tenant);
        apply(equipamento, request, codigo);
        return toDto(equipamentoRepository.save(equipamento));
    }

    public CmeEquipamentoDto update(Long id, CmeEquipamentoRequest request) {
        CmeEquipamento equipamento = equipamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Equipamento não encontrado"));
        tenantScopeGuard.checkRequestedTenant(equipamento.getTenant().getId());
        apply(equipamento, request, normalizeCodigo(request.codigo()));
        return toDto(equipamentoRepository.save(equipamento));
    }

    private void apply(CmeEquipamento equipamento, CmeEquipamentoRequest request, String codigo) {
        equipamento.setCodigo(codigo);
        equipamento.setNome(request.nome());
        equipamento.setTipoEquipamento(request.tipoEquipamento());
        equipamento.setEtapasPermitidas(request.etapasPermitidas() == null ? new HashSet<>() : new HashSet<>(request.etapasPermitidas()));
        equipamento.setFabricante(request.fabricante());
        equipamento.setModelo(request.modelo());
        equipamento.setNumeroSerie(request.numeroSerie());
        equipamento.setLocalizacao(request.localizacao());
        equipamento.setAtivo(request.ativo() == null || request.ativo());
        equipamento.setObservacoes(request.observacoes());
    }

    private String normalizeCodigo(String codigo) {
        return codigo == null ? null : codigo.trim().toUpperCase().replace(' ', '_');
    }

    private CmeEquipamentoDto toDto(CmeEquipamento e) {
        var etapasPermitidas = e.getEtapasPermitidas() == null
                ? new LinkedHashSet<CmeEtapaTipo>()
                : new LinkedHashSet<>(e.getEtapasPermitidas());
        return new CmeEquipamentoDto(e.getId(), e.getTenant().getId(), e.getCodigo(), e.getNome(), e.getTipoEquipamento(),
                etapasPermitidas, e.getFabricante(), e.getModelo(), e.getNumeroSerie(), e.getLocalizacao(),
                e.isAtivo(), e.getObservacoes());
    }
}
