package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.CicloLavadora;
import com.erp.qualitascareapi.cme.domain.MonitoramentoAmbiental;
import com.erp.qualitascareapi.cme.domain.RecebimentoMaterial;
import com.erp.qualitascareapi.cme.enums.RecebimentoStatus;
import com.erp.qualitascareapi.cme.repo.CicloLavadoraRepository;
import com.erp.qualitascareapi.cme.repo.MonitoramentoAmbientalRepository;
import com.erp.qualitascareapi.cme.repo.RecebimentoMaterialRepository;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProcessamentoService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final SetorRepository setorRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final RecebimentoMaterialRepository recebimentoRepository;
    private final CicloLavadoraRepository cicloLavadoraRepository;
    private final MonitoramentoAmbientalRepository monitoramentoRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public ProcessamentoService(TenantRepository tenantRepository,
                                UserRepository userRepository,
                                SetorRepository setorRepository,
                                EvidenciaArquivoRepository evidenciaArquivoRepository,
                                RecebimentoMaterialRepository recebimentoRepository,
                                CicloLavadoraRepository cicloLavadoraRepository,
                                MonitoramentoAmbientalRepository monitoramentoRepository,
                                TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.setorRepository = setorRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.recebimentoRepository = recebimentoRepository;
        this.cicloLavadoraRepository = cicloLavadoraRepository;
        this.monitoramentoRepository = monitoramentoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    // ---- Recebimento de Material ----

    public RecebimentoMaterialDto registrarRecebimento(RecebimentoMaterialRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        RecebimentoMaterial recebimento = new RecebimentoMaterial();
        recebimento.setTenant(tenant);
        recebimento.setDataHora(request.dataHora());
        if (request.setorOrigemId() != null) {
            Setor setor = setorRepository.findById(request.setorOrigemId())
                    .orElseThrow(() -> new EntityNotFoundException("Setor de origem não encontrado"));
            recebimento.setSetorOrigem(setor);
        }
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            recebimento.setResponsavel(responsavel);
        }
        recebimento.setQuantidadeItens(request.quantidadeItens());
        recebimento.setCondicaoDescricao(request.condicaoDescricao());
        recebimento.setStatus(request.status() != null ? request.status() : RecebimentoStatus.RECEBIDO);
        recebimento.setObservacoes(request.observacoes());
        recebimento.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toDto(recebimentoRepository.save(recebimento));
    }

    public Page<RecebimentoMaterialDto> listRecebimentos(Pageable pageable) {
        return recebimentoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toDto);
    }

    public RecebimentoMaterialDto findRecebimentoById(Long id) {
        return recebimentoRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Recebimento não encontrado"));
    }

    public RecebimentoMaterialDto updateRecebimentoStatus(Long id, RecebimentoStatus status) {
        RecebimentoMaterial r = recebimentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Recebimento não encontrado"));
        r.setStatus(status);
        return toDto(recebimentoRepository.save(r));
    }

    private RecebimentoMaterialDto toDto(RecebimentoMaterial r) {
        return new RecebimentoMaterialDto(r.getId(), r.getTenant().getId(), r.getDataHora(),
                r.getSetorOrigem() != null ? r.getSetorOrigem().getId() : null,
                r.getResponsavel() != null ? r.getResponsavel().getId() : null,
                r.getQuantidadeItens(), r.getCondicaoDescricao(), r.getStatus(),
                r.getObservacoes(), toIdSet(r.getEvidencias()));
    }

    // ---- Ciclo Lavadora Termodesinfetadora ----

    public CicloLavadoraDto registrarCicloLavadora(CicloLavadoraRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        CicloLavadora ciclo = new CicloLavadora();
        ciclo.setTenant(tenant);
        ciclo.setDataHora(request.dataHora());
        ciclo.setEquipamentoDescricao(request.equipamentoDescricao());
        ciclo.setNumeroCiclo(request.numeroCiclo());
        ciclo.setTemperaturaMaxima(request.temperaturaMaxima());
        ciclo.setDuracaoMinutos(request.duracaoMinutos());
        ciclo.setQuantidadeItens(request.quantidadeItens());
        ciclo.setResultado(request.resultado());
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            ciclo.setResponsavel(responsavel);
        }
        ciclo.setObservacoes(request.observacoes());
        ciclo.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toCicloDto(cicloLavadoraRepository.save(ciclo));
    }

    public Page<CicloLavadoraDto> listCiclosLavadora(Pageable pageable) {
        return cicloLavadoraRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toCicloDto);
    }

    public CicloLavadoraDto findCicloLavadoraById(Long id) {
        return cicloLavadoraRepository.findById(id)
                .map(this::toCicloDto)
                .orElseThrow(() -> new EntityNotFoundException("Ciclo de lavadora não encontrado"));
    }

    private CicloLavadoraDto toCicloDto(CicloLavadora c) {
        return new CicloLavadoraDto(c.getId(), c.getTenant().getId(), c.getDataHora(),
                c.getEquipamentoDescricao(), c.getNumeroCiclo(), c.getTemperaturaMaxima(),
                c.getDuracaoMinutos(), c.getQuantidadeItens(), c.getResultado(),
                c.getResponsavel() != null ? c.getResponsavel().getId() : null,
                c.getObservacoes(), toIdSet(c.getEvidencias()));
    }

    // ---- Monitoramento Ambiental ----

    public MonitoramentoAmbientalDto registrarMonitoramento(MonitoramentoAmbientalRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        MonitoramentoAmbiental monitoramento = new MonitoramentoAmbiental();
        monitoramento.setTenant(tenant);
        monitoramento.setDataHora(request.dataHora());
        monitoramento.setLocalSala(request.localSala());
        monitoramento.setTemperaturaCelsius(request.temperaturaCelsius());
        monitoramento.setUmidadeRelativa(request.umidadeRelativa());
        monitoramento.setPressaoDiferencialPa(request.pressaoDiferencialPa());
        monitoramento.setResultado(request.resultado());
        if (request.responsavelId() != null) {
            User responsavel = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            monitoramento.setResponsavel(responsavel);
        }
        monitoramento.setObservacoes(request.observacoes());
        monitoramento.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toMonitoramentoDto(monitoramentoRepository.save(monitoramento));
    }

    public Page<MonitoramentoAmbientalDto> listMonitoramentos(Pageable pageable) {
        return monitoramentoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable).map(this::toMonitoramentoDto);
    }

    public MonitoramentoAmbientalDto findMonitoramentoById(Long id) {
        return monitoramentoRepository.findById(id)
                .map(this::toMonitoramentoDto)
                .orElseThrow(() -> new EntityNotFoundException("Monitoramento não encontrado"));
    }

    private MonitoramentoAmbientalDto toMonitoramentoDto(MonitoramentoAmbiental m) {
        return new MonitoramentoAmbientalDto(m.getId(), m.getTenant().getId(), m.getDataHora(),
                m.getLocalSala(), m.getTemperaturaCelsius(), m.getUmidadeRelativa(),
                m.getPressaoDiferencialPa(), m.getResultado(),
                m.getResponsavel() != null ? m.getResponsavel().getId() : null,
                m.getObservacoes(), toIdSet(m.getEvidencias()));
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
