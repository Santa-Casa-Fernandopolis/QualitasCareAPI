package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.LoteEtiqueta;
import com.erp.qualitascareapi.cme.domain.MovimentacaoCME;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.cme.repo.LoteEtiquetaRepository;
import com.erp.qualitascareapi.cme.repo.MovimentacaoCMERepository;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.core.repo.KitVersionRepository;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
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

    public LoteService(TenantRepository tenantRepository,
                       UserRepository userRepository,
                       SetorRepository setorRepository,
                       LoteEtiquetaRepository loteEtiquetaRepository,
                       KitVersionRepository kitVersionRepository,
                       MovimentacaoCMERepository movimentacaoRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.setorRepository = setorRepository;
        this.loteEtiquetaRepository = loteEtiquetaRepository;
        this.kitVersionRepository = kitVersionRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public SetorDto createSetor(SetorRequest request) {
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

    public Page<SetorDto> listSetores(Pageable pageable) {
        return setorRepository.findAll(pageable)
                .map(s -> new SetorDto(s.getId(), s.getTenant().getId(), s.getNome(), s.getTipo(), s.getDescricao()));
    }

    public LoteEtiquetaDto createLote(LoteEtiquetaRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        LoteEtiqueta lote = new LoteEtiqueta();
        lote.setTenant(tenant);
        lote.setCodigo(request.codigo());
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
        lote.setObservacoes(request.observacoes());
        LoteEtiqueta saved = loteEtiquetaRepository.save(lote);
        Long montadoPor = saved.getMontadoPor() != null ? saved.getMontadoPor().getId() : null;
        Long kitVersaoId = saved.getKitVersao() != null ? saved.getKitVersao().getId() : null;
        return new LoteEtiquetaDto(saved.getId(), tenant.getId(), saved.getCodigo(), kitVersaoId,
                saved.getDataEmpacotamento(), saved.getValidade(), saved.getStatus(), saved.getQrCode(),
                montadoPor, saved.getObservacoes(), saved.getCriadoEm());
    }

    public Page<LoteEtiquetaDto> listLotes(Pageable pageable) {
        return loteEtiquetaRepository.findAll(pageable)
                .map(l -> new LoteEtiquetaDto(l.getId(), l.getTenant().getId(), l.getCodigo(),
                        l.getKitVersao() != null ? l.getKitVersao().getId() : null,
                        l.getDataEmpacotamento(), l.getValidade(), l.getStatus(), l.getQrCode(),
                        l.getMontadoPor() != null ? l.getMontadoPor().getId() : null,
                        l.getObservacoes(), l.getCriadoEm()));
    }

    public MovimentacaoDto registrarMovimentacao(MovimentacaoRequest request) {
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
        MovimentacaoCME saved = movimentacaoRepository.save(movimentacao);
        return new MovimentacaoDto(saved.getId(), tenant.getId(),
                saved.getLote() != null ? saved.getLote().getId() : null,
                saved.getSetorOrigem() != null ? saved.getSetorOrigem().getId() : null,
                saved.getSetorDestino() != null ? saved.getSetorDestino().getId() : null,
                saved.getTipo(), saved.getDataHora(),
                saved.getResponsavel() != null ? saved.getResponsavel().getId() : null,
                saved.getObservacoes());
    }

    public Page<MovimentacaoDto> listMovimentacoes(Pageable pageable) {
        return movimentacaoRepository.findAll(pageable)
                .map(m -> new MovimentacaoDto(m.getId(), m.getTenant().getId(),
                        m.getLote() != null ? m.getLote().getId() : null,
                        m.getSetorOrigem() != null ? m.getSetorOrigem().getId() : null,
                        m.getSetorDestino() != null ? m.getSetorDestino().getId() : null,
                        m.getTipo(), m.getDataHora(),
                        m.getResponsavel() != null ? m.getResponsavel().getId() : null,
                        m.getObservacoes()));
    }
}
