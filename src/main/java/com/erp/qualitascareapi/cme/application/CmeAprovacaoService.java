package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.approval.core.domain.ApprovalFlowDef;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.approval.core.repo.ApprovalFlowDefRepository;
import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.AprovacaoCicloEsterilizacao;
import com.erp.qualitascareapi.cme.domain.AprovacaoLoteInalatorio;
import com.erp.qualitascareapi.cme.domain.CicloEsterilizacao;
import com.erp.qualitascareapi.cme.domain.LoteEtiqueta;
import com.erp.qualitascareapi.cme.repo.AprovacaoCicloEsterilizacaoRepository;
import com.erp.qualitascareapi.cme.repo.AprovacaoLoteInalatorioRepository;
import com.erp.qualitascareapi.cme.repo.CicloEsterilizacaoRepository;
import com.erp.qualitascareapi.cme.repo.LoteEtiquetaRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CmeAprovacaoService {

    private static final Set<ApprovalDomain> CME_DOMAINS = Set.of(
            ApprovalDomain.CICLO_ESTERILIZACAO, ApprovalDomain.LOTE_INHALATORIO);

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ApprovalFlowDefRepository flowDefRepository;
    private final CicloEsterilizacaoRepository cicloRepository;
    private final LoteEtiquetaRepository loteRepository;
    private final AprovacaoCicloEsterilizacaoRepository aprovacaoCicloRepository;
    private final AprovacaoLoteInalatorioRepository aprovacaoLoteRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public CmeAprovacaoService(TenantRepository tenantRepository,
                               UserRepository userRepository,
                               ApprovalFlowDefRepository flowDefRepository,
                               CicloEsterilizacaoRepository cicloRepository,
                               LoteEtiquetaRepository loteRepository,
                               AprovacaoCicloEsterilizacaoRepository aprovacaoCicloRepository,
                               AprovacaoLoteInalatorioRepository aprovacaoLoteRepository,
                               TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.flowDefRepository = flowDefRepository;
        this.cicloRepository = cicloRepository;
        this.loteRepository = loteRepository;
        this.aprovacaoCicloRepository = aprovacaoCicloRepository;
        this.aprovacaoLoteRepository = aprovacaoLoteRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    // ---- ApprovalFlowDef (filtrado pelos domínios CME) ----

    public List<CmeFlowDefDto> listFlowDefs() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return flowDefRepository.findAll().stream()
                .filter(f -> f.getTenant().getId().equals(tenantId) && CME_DOMAINS.contains(f.getDomain()))
                .map(this::toFlowDefDto)
                .toList();
    }

    public CmeFlowDefDto createFlowDef(CmeFlowDefRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        ApprovalDomain domain = ApprovalDomain.valueOf(request.tipoRecurso());
        if (!CME_DOMAINS.contains(domain)) {
            throw new IllegalArgumentException("Tipo de recurso inválido para CME: " + request.tipoRecurso());
        }
        ApprovalFlowDef flow = new ApprovalFlowDef();
        flow.setTenant(tenant);
        flow.setDomain(domain);
        flow.setName(request.nome());
        flow.setActive(request.ativo() != null ? request.ativo() : Boolean.TRUE);
        return toFlowDefDto(flowDefRepository.save(flow));
    }

    // ---- Aprovação de Ciclo de Esterilização ----

    public Page<AprovacaoCicloDto> listAprovacoesCiclo(Pageable pageable) {
        return aprovacaoCicloRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toAprovacaoCicloDto);
    }

    public AprovacaoCicloDto registrarAprovacaoCiclo(AprovacaoCicloRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        CicloEsterilizacao ciclo = cicloRepository.findById(request.cicloId())
                .orElseThrow(() -> new EntityNotFoundException("Ciclo de esterilização não encontrado"));
        AprovacaoCicloEsterilizacao aprovacao = new AprovacaoCicloEsterilizacao();
        aprovacao.setTenant(tenant);
        aprovacao.setCiclo(ciclo);
        if (request.flowDefId() != null) {
            flowDefRepository.findById(request.flowDefId()).ifPresent(aprovacao::setFlowDef);
        }
        aprovacao.setStatus(request.status());
        if (request.aprovadoPorId() != null) {
            User user = userRepository.findById(request.aprovadoPorId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário aprovador não encontrado"));
            aprovacao.setAprovadoPor(user);
        }
        aprovacao.setDataAprovacao(request.dataAprovacao());
        aprovacao.setComentario(request.comentario());
        return toAprovacaoCicloDto(aprovacaoCicloRepository.save(aprovacao));
    }

    // ---- Aprovação de Lote Inalatório ----

    public Page<AprovacaoLoteInalatorioDto> listAprovacoesCicloLote(Pageable pageable) {
        return aprovacaoLoteRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toAprovacaoLoteDto);
    }

    public AprovacaoLoteInalatorioDto registrarAprovacaoLote(AprovacaoLoteInalatorioRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        LoteEtiqueta lote = loteRepository.findById(request.loteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
        AprovacaoLoteInalatorio aprovacao = new AprovacaoLoteInalatorio();
        aprovacao.setTenant(tenant);
        aprovacao.setLote(lote);
        if (request.flowDefId() != null) {
            flowDefRepository.findById(request.flowDefId()).ifPresent(aprovacao::setFlowDef);
        }
        aprovacao.setTipoMaterial(request.tipoMaterial());
        aprovacao.setStatus(request.status());
        if (request.aprovadoPorId() != null) {
            User user = userRepository.findById(request.aprovadoPorId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário aprovador não encontrado"));
            aprovacao.setAprovadoPor(user);
        }
        aprovacao.setDataAprovacao(request.dataAprovacao());
        aprovacao.setComentario(request.comentario());
        return toAprovacaoLoteDto(aprovacaoLoteRepository.save(aprovacao));
    }

    // ---- Mappers ----

    private CmeFlowDefDto toFlowDefDto(ApprovalFlowDef f) {
        return new CmeFlowDefDto(f.getId(), f.getTenant().getId(), f.getName(),
                f.getDomain().name(), false, f.getActive());
    }

    private AprovacaoCicloDto toAprovacaoCicloDto(AprovacaoCicloEsterilizacao a) {
        return new AprovacaoCicloDto(a.getId(), a.getTenant().getId(),
                a.getCiclo().getId(),
                a.getFlowDef() != null ? a.getFlowDef().getId() : null,
                a.getStatus(),
                a.getAprovadoPor() != null ? a.getAprovadoPor().getId() : null,
                a.getDataAprovacao(), a.getComentario());
    }

    private AprovacaoLoteInalatorioDto toAprovacaoLoteDto(AprovacaoLoteInalatorio a) {
        return new AprovacaoLoteInalatorioDto(a.getId(), a.getTenant().getId(),
                a.getLote().getId(),
                a.getFlowDef() != null ? a.getFlowDef().getId() : null,
                a.getTipoMaterial(), a.getStatus(),
                a.getAprovadoPor() != null ? a.getAprovadoPor().getId() : null,
                a.getDataAprovacao(), a.getComentario());
    }
}
