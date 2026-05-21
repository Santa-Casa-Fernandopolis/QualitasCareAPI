package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.LoteEtiqueta;
import com.erp.qualitascareapi.cme.domain.MovimentacaoCME;
import com.erp.qualitascareapi.cme.domain.ProcessoReprocessamento;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.cme.repo.LoteEtiquetaRepository;
import com.erp.qualitascareapi.cme.repo.MovimentacaoCMERepository;
import com.erp.qualitascareapi.cme.repo.ProcessoReprocessamentoRepository;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.core.repo.KitVersionRepository;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.integracao.mv.domain.CirurgiaAgendada;
import com.erp.qualitascareapi.integracao.mv.repo.CirurgiaAgendadaRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoteService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SetorRepository setorRepository;
    private final LoteEtiquetaRepository loteEtiquetaRepository;
    private final KitVersionRepository kitVersionRepository;
    private final MovimentacaoCMERepository movimentacaoRepository;
    private final ProcessoReprocessamentoRepository processoRepository;
    private final CirurgiaAgendadaRepository cirurgiaAgendadaRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public LoteService(TenantRepository tenantRepository,
                       UserRepository userRepository,
                       SetorRepository setorRepository,
                       LoteEtiquetaRepository loteEtiquetaRepository,
                       KitVersionRepository kitVersionRepository,
                       MovimentacaoCMERepository movimentacaoRepository,
                       ProcessoReprocessamentoRepository processoRepository,
                       CirurgiaAgendadaRepository cirurgiaAgendadaRepository,
                       TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.setorRepository = setorRepository;
        this.loteEtiquetaRepository = loteEtiquetaRepository;
        this.kitVersionRepository = kitVersionRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.processoRepository = processoRepository;
        this.cirurgiaAgendadaRepository = cirurgiaAgendadaRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public SetorDto createSetor(SetorRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        Setor setor = new Setor();
        setor.setTenant(tenant);
        setor.setNome(request.nome());
        setor.setTipo(request.tipo());
        setor.setDescricao(request.descricao());
        Setor saved = setorRepository.save(setor);
        return new SetorDto(saved.getId(), tenant.getId(), saved.getNome(), saved.getTipo(), saved.getDescricao());
    }

    public SetorDto findSetorById(Long id) {
        Setor s = setorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Setor não encontrado"));
        return new SetorDto(s.getId(), s.getTenant().getId(), s.getNome(), s.getTipo(), s.getDescricao());
    }

    public Page<SetorDto> listSetores(Pageable pageable) {
        return setorRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(s -> new SetorDto(s.getId(), s.getTenant().getId(), s.getNome(), s.getTipo(), s.getDescricao()));
    }

    public LoteEtiquetaDto createLote(LoteEtiquetaRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        LoteEtiqueta lote = new LoteEtiqueta();
        lote.setTenant(tenant);
        lote.setCodigo(request.codigo());
        if (request.processoId() != null) {
            ProcessoReprocessamento processo = processoRepository.findById(request.processoId())
                    .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
            lote.setProcesso(processo);
        }
        if (request.kitVersaoId() != null) {
            KitVersion versao = kitVersionRepository.findById(request.kitVersaoId())
                    .orElseThrow(() -> new EntityNotFoundException("Versão de kit não encontrada"));
            lote.setKitVersao(versao);
        }
        lote.setDataEmpacotamento(request.dataEmpacotamento());
        lote.setValidade(request.validade());
        lote.setStatus(request.status() != null ? request.status() : LoteStatus.MONTADO);
        lote.setQrCode(request.qrCode());
        if (request.montadoPorId() != null) {
            User usuario = userRepository.findById(request.montadoPorId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            lote.setMontadoPor(usuario);
        }
        lote.setDataHoraInicioMontagem(request.dataHoraInicioMontagem());
        lote.setDataHoraFimMontagem(request.dataHoraFimMontagem());
        lote.setObservacoes(request.observacoes());
        return toLoteDto(loteEtiquetaRepository.save(lote));
    }

    public LoteEtiquetaDto findLoteById(Long id) {
        LoteEtiqueta l = loteEtiquetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
        return toLoteDto(l);
    }

    public LoteEtiquetaDto updateLoteStatus(Long id, com.erp.qualitascareapi.cme.enums.LoteStatus status) {
        LoteEtiqueta l = loteEtiquetaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
        l.setStatus(status);
        return toLoteDto(loteEtiquetaRepository.save(l));
    }

    private LoteEtiquetaDto toLoteDto(LoteEtiqueta l) {
        return new LoteEtiquetaDto(l.getId(), l.getTenant().getId(),
                l.getProcesso() != null ? l.getProcesso().getId() : null,
                l.getCodigo(),
                l.getKitVersao() != null ? l.getKitVersao().getId() : null,
                l.getDataEmpacotamento(), l.getValidade(), l.getStatus(), l.getQrCode(),
                l.getMontadoPor() != null ? l.getMontadoPor().getId() : null,
                l.getDataHoraInicioMontagem(), l.getDataHoraFimMontagem(),
                l.getObservacoes(), l.getCriadoEm());
    }

    public Page<LoteEtiquetaDto> listLotes(Pageable pageable) {
        return loteEtiquetaRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toLoteDto);
    }

    public MovimentacaoDto registrarMovimentacao(MovimentacaoRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        MovimentacaoCME movimentacao = new MovimentacaoCME();
        movimentacao.setTenant(tenant);
        if (request.loteId() != null) {
            LoteEtiqueta lote = loteEtiquetaRepository.findById(request.loteId())
                    .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
            movimentacao.setLote(lote);
        }
        if (request.setorOrigemId() != null) {
            Setor origem = setorRepository.findById(request.setorOrigemId())
                    .orElseThrow(() -> new EntityNotFoundException("Setor de origem não encontrado"));
            movimentacao.setSetorOrigem(origem);
        }
        if (request.setorDestinoId() != null) {
            Setor destino = setorRepository.findById(request.setorDestinoId())
                    .orElseThrow(() -> new EntityNotFoundException("Setor de destino não encontrado"));
            movimentacao.setSetorDestino(destino);
        }
        movimentacao.setTipo(request.tipo());
        movimentacao.setDataHora(request.dataHora());
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            movimentacao.setResponsavel(responsavel);
        }
        movimentacao.setObservacoes(request.observacoes());

        // Vínculo com cirurgia agendada (MV) — opcional
        if (request.cirurgiaId() != null) {
            CirurgiaAgendada cirurgia = cirurgiaAgendadaRepository.findById(request.cirurgiaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Cirurgia agendada não encontrada: id=" + request.cirurgiaId()));
            // Garante que a cirurgia pertence ao mesmo tenant
            if (!cirurgia.getTenantId().equals(request.tenantId())) {
                throw new IllegalArgumentException(
                        "Cirurgia agendada id=" + request.cirurgiaId() + " não pertence ao tenant informado.");
            }
            movimentacao.setCirurgia(cirurgia);
        }

        MovimentacaoCME saved = movimentacaoRepository.save(movimentacao);
        return toMovimentacaoDto(saved);
    }

    public Page<MovimentacaoDto> listMovimentacoes(Pageable pageable) {
        return movimentacaoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toMovimentacaoDto);
    }

    private MovimentacaoDto toMovimentacaoDto(MovimentacaoCME m) {
        CirurgiaAgendada cir = m.getCirurgia();
        return new MovimentacaoDto(
                m.getId(),
                m.getTenant().getId(),
                m.getLote() != null ? m.getLote().getId() : null,
                m.getSetorOrigem() != null ? m.getSetorOrigem().getId() : null,
                m.getSetorDestino() != null ? m.getSetorDestino().getId() : null,
                m.getTipo(),
                m.getDataHora(),
                m.getResponsavel() != null ? m.getResponsavel().getId() : null,
                m.getObservacoes(),
                // cirurgia
                cir != null ? cir.getId() : null,
                cir != null ? cir.getCodigoPaciente() : null,
                cir != null ? cir.getNomePaciente() : null,
                cir != null ? cir.getTipoCirurgia() : null,
                cir != null ? cir.getSalaCirurgica() : null,
                cir != null ? cir.getDataHoraInicio() : null
        );
    }
}
