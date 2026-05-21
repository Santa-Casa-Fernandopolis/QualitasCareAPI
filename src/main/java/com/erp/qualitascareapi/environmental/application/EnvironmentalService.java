package com.erp.qualitascareapi.environmental.application;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.environmental.api.dto.*;
import com.erp.qualitascareapi.environmental.domain.GeladeiraMedicamentos;
import com.erp.qualitascareapi.environmental.domain.MonitoramentoAmbiental;
import com.erp.qualitascareapi.environmental.domain.RegistroTemperaturaGeladeira;
import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.environmental.repo.GeladeiraMedicamentosRepository;
import com.erp.qualitascareapi.environmental.repo.MonitoramentoAmbientalRepository;
import com.erp.qualitascareapi.environmental.repo.RegistroTemperaturaGeladeiraRepository;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class EnvironmentalService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final MonitoramentoAmbientalRepository monitoramentoRepository;
    private final GeladeiraMedicamentosRepository geladeiraRepository;
    private final RegistroTemperaturaGeladeiraRepository registroRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public EnvironmentalService(TenantRepository tenantRepository,
                                UserRepository userRepository,
                                EvidenciaArquivoRepository evidenciaArquivoRepository,
                                MonitoramentoAmbientalRepository monitoramentoRepository,
                                GeladeiraMedicamentosRepository geladeiraRepository,
                                RegistroTemperaturaGeladeiraRepository registroRepository,
                                TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.monitoramentoRepository = monitoramentoRepository;
        this.geladeiraRepository = geladeiraRepository;
        this.registroRepository = registroRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    // ---- Monitoramento Ambiental ----

    public MonitoramentoAmbientalDto registrarMonitoramento(MonitoramentoAmbientalRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));

        MonitoramentoAmbiental m = new MonitoramentoAmbiental();
        m.setTenant(tenant);
        m.setDataHora(request.dataHora());
        m.setTipoAmbiente(request.tipoAmbiente());
        m.setLocalSala(request.localSala());
        m.setTemperaturaCelsius(request.temperaturaCelsius());
        m.setUmidadeRelativa(request.umidadeRelativa());
        m.setPressaoDiferencialPa(request.pressaoDiferencialPa());
        m.setResultado(request.resultado());
        if (request.responsavelId() != null) {
            m.setResponsavel(loadUser(request.responsavelId()));
        }
        m.setObservacoes(request.observacoes());
        m.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toMonitoramentoDto(monitoramentoRepository.save(m));
    }

    public Page<MonitoramentoAmbientalDto> listMonitoramentos(Pageable pageable) {
        return monitoramentoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toMonitoramentoDto);
    }

    public MonitoramentoAmbientalDto findMonitoramentoById(Long id) {
        return monitoramentoRepository.findById(id)
                .map(this::toMonitoramentoDto)
                .orElseThrow(() -> new EntityNotFoundException("Monitoramento ambiental não encontrado"));
    }

    private MonitoramentoAmbientalDto toMonitoramentoDto(MonitoramentoAmbiental m) {
        return new MonitoramentoAmbientalDto(
                m.getId(), m.getTenant().getId(), m.getDataHora(),
                m.getTipoAmbiente(), m.getLocalSala(),
                m.getTemperaturaCelsius(), m.getUmidadeRelativa(), m.getPressaoDiferencialPa(),
                m.getResultado(),
                m.getResponsavel() != null ? m.getResponsavel().getId() : null,
                m.getObservacoes(), toIdSet(m.getEvidencias()));
    }

    // ---- Geladeira de Medicamentos / Vacinas ----

    public GeladeiraMedicamentosDto cadastrarGeladeira(GeladeiraMedicamentosRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));

        GeladeiraMedicamentos g = new GeladeiraMedicamentos();
        g.setTenant(tenant);
        g.setNome(request.nome());
        g.setTipoUso(request.tipoUso());
        g.setLocalSala(request.localSala());
        g.setFabricante(request.fabricante());
        g.setModelo(request.modelo());
        g.setNumeroSerie(request.numeroSerie());
        g.setTemperaturaMinCelsius(request.temperaturaMinCelsius());
        g.setTemperaturaMaxCelsius(request.temperaturaMaxCelsius());
        g.setFrequenciaLeituraHoras(request.frequenciaLeituraHoras());
        g.setAtivo(request.ativo() != null ? request.ativo() : true);
        g.setObservacoes(request.observacoes());
        return toGeladeiraDto(geladeiraRepository.save(g));
    }

    public GeladeiraMedicamentosDto updateGeladeira(Long id, GeladeiraMedicamentosRequest request) {
        GeladeiraMedicamentos g = geladeiraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada"));
        tenantScopeGuard.checkRequestedTenant(g.getTenant().getId());

        g.setNome(request.nome());
        g.setTipoUso(request.tipoUso());
        g.setLocalSala(request.localSala());
        g.setFabricante(request.fabricante());
        g.setModelo(request.modelo());
        g.setNumeroSerie(request.numeroSerie());
        g.setTemperaturaMinCelsius(request.temperaturaMinCelsius());
        g.setTemperaturaMaxCelsius(request.temperaturaMaxCelsius());
        g.setFrequenciaLeituraHoras(request.frequenciaLeituraHoras());
        if (request.ativo() != null) g.setAtivo(request.ativo());
        g.setObservacoes(request.observacoes());
        return toGeladeiraDto(geladeiraRepository.save(g));
    }

    public GeladeiraMedicamentosDto toggleGeladeiraStatus(Long id, boolean ativo) {
        GeladeiraMedicamentos g = geladeiraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada"));
        tenantScopeGuard.checkRequestedTenant(g.getTenant().getId());
        g.setAtivo(ativo);
        return toGeladeiraDto(geladeiraRepository.save(g));
    }

    public Page<GeladeiraMedicamentosDto> listGeladeiras(Pageable pageable) {
        return geladeiraRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toGeladeiraDto);
    }

    public GeladeiraMedicamentosDto findGeladeiraById(Long id) {
        return geladeiraRepository.findById(id)
                .map(this::toGeladeiraDto)
                .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada"));
    }

    private GeladeiraMedicamentosDto toGeladeiraDto(GeladeiraMedicamentos g) {
        return new GeladeiraMedicamentosDto(
                g.getId(), g.getTenant().getId(), g.getNome(), g.getTipoUso(),
                g.getLocalSala(), g.getFabricante(), g.getModelo(), g.getNumeroSerie(),
                g.getTemperaturaMinCelsius(), g.getTemperaturaMaxCelsius(),
                g.getFrequenciaLeituraHoras(), g.isAtivo(), g.getObservacoes());
    }

    // ---- Registro de Temperatura de Geladeira ----

    public RegistroTemperaturaGeladeiraDto registrarTemperatura(RegistroTemperaturaGeladeiraRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        GeladeiraMedicamentos geladeira = geladeiraRepository.findById(request.geladeiraId())
                .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada"));

        RegistroTemperaturaGeladeira r = new RegistroTemperaturaGeladeira();
        r.setTenant(tenant);
        r.setGeladeira(geladeira);
        r.setDataHora(request.dataHora());
        r.setTemperaturaCelsius(request.temperaturaCelsius());
        r.setUmidadeRelativa(request.umidadeRelativa());

        // Auto-avalia o resultado com base nos limites da geladeira (se não fornecido)
        ResultadoMonitoramento resultado = request.resultado();
        if (resultado == null) {
            resultado = avaliarResultado(request.temperaturaCelsius(), geladeira);
        }
        r.setResultado(resultado);
        r.setAcaoCorretiva(request.acaoCorretiva());
        if (request.responsavelId() != null) {
            r.setResponsavel(loadUser(request.responsavelId()));
        }
        r.setObservacoes(request.observacoes());
        return toRegistroDto(registroRepository.save(r));
    }

    public Page<RegistroTemperaturaGeladeiraDto> listRegistros(Long geladeiraId, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Page<RegistroTemperaturaGeladeira> page = geladeiraId != null
                ? registroRepository.findAllByTenantIdAndGeladeiraId(tenantId, geladeiraId, pageable)
                : registroRepository.findAllByTenantId(tenantId, pageable);
        return page.map(this::toRegistroDto);
    }

    public RegistroTemperaturaGeladeiraDto findRegistroById(Long id) {
        return registroRepository.findById(id)
                .map(this::toRegistroDto)
                .orElseThrow(() -> new EntityNotFoundException("Registro de temperatura não encontrado"));
    }

    /** Avalia o resultado automaticamente comparando a temperatura lida com os limites da geladeira. */
    private ResultadoMonitoramento avaliarResultado(double temp, GeladeiraMedicamentos geladeira) {
        Double min = geladeira.getTemperaturaMinCelsius();
        Double max = geladeira.getTemperaturaMaxCelsius();
        if (min == null || max == null) return ResultadoMonitoramento.CONFORME;

        // Margem de alerta: 10% da faixa total
        double faixa = max - min;
        double margem = faixa * 0.10;

        if (temp < min || temp > max) return ResultadoMonitoramento.NAO_CONFORME;
        if (temp < (min + margem) || temp > (max - margem)) return ResultadoMonitoramento.ALERTA;
        return ResultadoMonitoramento.CONFORME;
    }

    private RegistroTemperaturaGeladeiraDto toRegistroDto(RegistroTemperaturaGeladeira r) {
        return new RegistroTemperaturaGeladeiraDto(
                r.getId(), r.getTenant().getId(),
                r.getGeladeira().getId(), r.getGeladeira().getNome(),
                r.getDataHora(), r.getTemperaturaCelsius(), r.getUmidadeRelativa(),
                r.getResultado(), r.getAcaoCorretiva(),
                r.getResponsavel() != null ? r.getResponsavel().getId() : null,
                r.getObservacoes());
    }

    // ---- helpers ----

    private User loadUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
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
