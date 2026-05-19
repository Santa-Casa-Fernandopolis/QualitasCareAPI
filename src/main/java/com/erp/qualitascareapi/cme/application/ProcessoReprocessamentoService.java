package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.*;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import com.erp.qualitascareapi.cme.repo.*;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProcessoReprocessamentoService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ProcessoReprocessamentoRepository processoRepository;
    private final LimpezaManualRepository limpezaManualRepository;
    private final HigienizacaoUltrassonicaRepository higienizacaoUltrassonicaRepository;
    private final LoteEtiquetaRepository loteEtiquetaRepository;
    private final CicloEsterilizacaoRepository cicloEsterilizacaoRepository;
    private final RecebimentoMaterialRepository recebimentoRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;

    public ProcessoReprocessamentoService(TenantRepository tenantRepository,
                                          UserRepository userRepository,
                                          ProcessoReprocessamentoRepository processoRepository,
                                          LimpezaManualRepository limpezaManualRepository,
                                          HigienizacaoUltrassonicaRepository higienizacaoUltrassonicaRepository,
                                          LoteEtiquetaRepository loteEtiquetaRepository,
                                          CicloEsterilizacaoRepository cicloEsterilizacaoRepository,
                                          RecebimentoMaterialRepository recebimentoRepository,
                                          EvidenciaArquivoRepository evidenciaArquivoRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.processoRepository = processoRepository;
        this.limpezaManualRepository = limpezaManualRepository;
        this.higienizacaoUltrassonicaRepository = higienizacaoUltrassonicaRepository;
        this.loteEtiquetaRepository = loteEtiquetaRepository;
        this.cicloEsterilizacaoRepository = cicloEsterilizacaoRepository;
        this.recebimentoRepository = recebimentoRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
    }

    // ---- ProcessoReprocessamento CRUD ----

    public ProcessoReprocessamentoDto createProcesso(ProcessoReprocessamentoRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        ProcessoReprocessamento processo = new ProcessoReprocessamento();
        processo.setTenant(tenant);
        processo.setNumeroProcesso(request.numeroProcesso());
        processo.setStatus(request.status() != null ? request.status() : ProcessoStatus.ABERTO);
        processo.setDataAbertura(request.dataAbertura());
        processo.setDataConclusao(request.dataConclusao());
        if (request.recebimentoId() != null) {
            RecebimentoMaterial recebimento = recebimentoRepository.findById(request.recebimentoId())
                    .orElseThrow(() -> new EntityNotFoundException("Recebimento não encontrado"));
            processo.setRecebimento(recebimento);
        }
        processo.setObservacoes(request.observacoes());
        return toProcessoDto(processoRepository.save(processo));
    }

    public Page<ProcessoReprocessamentoDto> listProcessos(Pageable pageable) {
        return processoRepository.findAll(pageable).map(this::toProcessoDto);
    }

    public ProcessoReprocessamentoDto findProcessoById(Long id) {
        return processoRepository.findById(id)
                .map(this::toProcessoDto)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
    }

    public ProcessoReprocessamentoDto updateProcessoStatus(Long id, ProcessoStatus status) {
        ProcessoReprocessamento processo = processoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
        processo.setStatus(status);
        return toProcessoDto(processoRepository.save(processo));
    }

    // ---- LimpezaManual CRUD ----

    public LimpezaManualDto registrarLimpezaManual(LimpezaManualRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        User responsavel = userRepository.findById(request.responsavelId())
                .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
        LimpezaManual limpeza = new LimpezaManual();
        limpeza.setTenant(tenant);
        limpeza.setResponsavel(responsavel);
        if (request.processoId() != null) {
            ProcessoReprocessamento processo = processoRepository.findById(request.processoId())
                    .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));
            limpeza.setProcesso(processo);
        }
        limpeza.setDataHoraInicio(request.dataHoraInicio());
        limpeza.setDataHoraFim(request.dataHoraFim());
        limpeza.setProdutoUtilizado(request.produtoUtilizado());
        limpeza.setConcentracao(request.concentracao());
        limpeza.setMetodo(request.metodo());
        limpeza.setConformidade(request.conformidade());
        limpeza.setObservacoes(request.observacoes());
        limpeza.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toLimpezaDto(limpezaManualRepository.save(limpeza));
    }

    public Page<LimpezaManualDto> listLimpezas(Pageable pageable) {
        return limpezaManualRepository.findAll(pageable).map(this::toLimpezaDto);
    }

    public LimpezaManualDto findLimpezaById(Long id) {
        return limpezaManualRepository.findById(id)
                .map(this::toLimpezaDto)
                .orElseThrow(() -> new EntityNotFoundException("Limpeza manual não encontrada"));
    }

    // ---- Timeline ----

    public ProcessoTimelineDto getTimeline(Long processoId) {
        ProcessoReprocessamento processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new EntityNotFoundException("Processo não encontrado"));

        RecebimentoMaterialDto recebimentoDto = null;
        if (processo.getRecebimento() != null) {
            RecebimentoMaterial r = processo.getRecebimento();
            recebimentoDto = new RecebimentoMaterialDto(r.getId(), r.getTenant().getId(), r.getDataHora(),
                    r.getSetorOrigem() != null ? r.getSetorOrigem().getId() : null,
                    r.getResponsavel() != null ? r.getResponsavel().getId() : null,
                    r.getQuantidadeItens(), r.getCondicaoDescricao(), r.getStatus(),
                    r.getObservacoes(), toIdSet(r.getEvidencias()));
        }

        List<LimpezaManualDto> limpezasDto = limpezaManualRepository.findByProcessoId(processoId)
                .stream().map(this::toLimpezaDto).toList();

        List<HigienizacaoUltrassonicaDto> ultrassonicasDto = higienizacaoUltrassonicaRepository.findByProcessoId(processoId)
                .stream().map(this::toUltrassonicaDto).toList();

        List<LoteEtiquetaDto> lotesDto = loteEtiquetaRepository.findByProcessoId(processoId)
                .stream().map(this::toLoteDto).toList();

        List<CicloEsterilizacaoDto> ciclosDto = cicloEsterilizacaoRepository.findByProcessoId(processoId)
                .stream().map(this::toCicloDto).toList();

        return new ProcessoTimelineDto(toProcessoDto(processo), recebimentoDto, limpezasDto,
                ultrassonicasDto, lotesDto, ciclosDto);
    }

    // ---- Private mappers ----

    private ProcessoReprocessamentoDto toProcessoDto(ProcessoReprocessamento p) {
        return new ProcessoReprocessamentoDto(p.getId(), p.getTenant().getId(), p.getNumeroProcesso(),
                p.getStatus(), p.getDataAbertura(), p.getDataConclusao(),
                p.getRecebimento() != null ? p.getRecebimento().getId() : null,
                p.getObservacoes());
    }

    private LimpezaManualDto toLimpezaDto(LimpezaManual l) {
        return new LimpezaManualDto(l.getId(), l.getTenant().getId(),
                l.getProcesso() != null ? l.getProcesso().getId() : null,
                l.getResponsavel().getId(),
                l.getDataHoraInicio(), l.getDataHoraFim(),
                l.getProdutoUtilizado(), l.getConcentracao(),
                l.getMetodo(), l.getConformidade(), l.getObservacoes(),
                toIdSet(l.getEvidencias()));
    }

    private HigienizacaoUltrassonicaDto toUltrassonicaDto(HigienizacaoUltrassonica h) {
        return new HigienizacaoUltrassonicaDto(h.getId(), h.getTenant().getId(),
                h.getProcesso() != null ? h.getProcesso().getId() : null,
                h.getDataRealizacao(), h.getDataHoraInicio(), h.getDataHoraFim(),
                h.getEquipamentoDescricao(),
                h.getResponsavel() != null ? h.getResponsavel().getId() : null,
                h.getObservacoes(), toIdSet(h.getEvidencias()));
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

    private CicloEsterilizacaoDto toCicloDto(CicloEsterilizacao c) {
        return new CicloEsterilizacaoDto(c.getId(), c.getTenant().getId(),
                c.getProcesso() != null ? c.getProcesso().getId() : null,
                c.getAutoclave().getId(),
                c.getLoteEtiqueta() != null ? c.getLoteEtiqueta().getId() : null,
                c.getInicio(), c.getFim(), c.getDuracaoMinutos(), c.getTemperaturaMaxima(),
                c.getPressaoMaxima(), c.getStatus(),
                c.getLiberadoPor() != null ? c.getLiberadoPor().getId() : null,
                c.getObservacoes());
    }

    private Set<EvidenciaArquivo> loadEvidencias(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) return Collections.emptySet();
        return new HashSet<>(evidenciaArquivoRepository.findAllById(ids));
    }

    private Set<Long> toIdSet(Set<EvidenciaArquivo> evidencias) {
        if (evidencias == null || evidencias.isEmpty()) return Collections.emptySet();
        return evidencias.stream().map(EvidenciaArquivo::getId).collect(Collectors.toSet());
    }
}
