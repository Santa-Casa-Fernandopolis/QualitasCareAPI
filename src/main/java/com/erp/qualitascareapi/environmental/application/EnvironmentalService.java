package com.erp.qualitascareapi.environmental.application;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.environmental.api.dto.*;
import com.erp.qualitascareapi.environmental.domain.*;
import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.environmental.enums.TipoDispositivoIoT;
import com.erp.qualitascareapi.environmental.repo.*;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class EnvironmentalService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final AmbienteRepository ambienteRepository;
    private final MonitoramentoAmbientalRepository monitoramentoRepository;
    private final GeladeiraMedicamentosRepository geladeiraRepository;
    private final RegistroTemperaturaGeladeiraRepository registroRepository;
    private final DispositivoIoTRepository dispositivoRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public EnvironmentalService(TenantRepository tenantRepository,
                                UserRepository userRepository,
                                EvidenciaArquivoRepository evidenciaArquivoRepository,
                                AmbienteRepository ambienteRepository,
                                MonitoramentoAmbientalRepository monitoramentoRepository,
                                GeladeiraMedicamentosRepository geladeiraRepository,
                                RegistroTemperaturaGeladeiraRepository registroRepository,
                                DispositivoIoTRepository dispositivoRepository,
                                TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.ambienteRepository = ambienteRepository;
        this.monitoramentoRepository = monitoramentoRepository;
        this.geladeiraRepository = geladeiraRepository;
        this.registroRepository = registroRepository;
        this.dispositivoRepository = dispositivoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    // ============================================================
    // Ambiente (salas e áreas monitoradas)
    // ============================================================

    public AmbienteDto cadastrarAmbiente(AmbienteRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = loadTenant(request.tenantId());

        Ambiente a = new Ambiente();
        a.setTenant(tenant);
        applyAmbienteFields(a, request);
        return toAmbienteDto(ambienteRepository.save(a));
    }

    public AmbienteDto updateAmbiente(Long id, AmbienteRequest request) {
        Ambiente a = ambienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado"));
        tenantScopeGuard.checkRequestedTenant(a.getTenant().getId());
        applyAmbienteFields(a, request);
        return toAmbienteDto(ambienteRepository.save(a));
    }

    public AmbienteDto toggleAmbienteStatus(Long id, boolean ativo) {
        Ambiente a = ambienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado"));
        tenantScopeGuard.checkRequestedTenant(a.getTenant().getId());
        a.setAtivo(ativo);
        return toAmbienteDto(ambienteRepository.save(a));
    }

    @Transactional(readOnly = true)
    public Page<AmbienteDto> listAmbientes(Boolean ativo, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Page<Ambiente> page = ativo != null
                ? ambienteRepository.findAllByTenantIdAndAtivo(tenantId, ativo, pageable)
                : ambienteRepository.findAllByTenantId(tenantId, pageable);
        return page.map(this::toAmbienteDto);
    }

    @Transactional(readOnly = true)
    public AmbienteDto findAmbienteById(Long id) {
        return ambienteRepository.findById(id)
                .map(this::toAmbienteDto)
                .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado"));
    }

    private void applyAmbienteFields(Ambiente a, AmbienteRequest r) {
        a.setNome(r.nome());
        a.setTipoAmbiente(r.tipoAmbiente());
        a.setBloco(r.bloco());
        a.setAndar(r.andar());
        a.setSetor(r.setor());
        a.setTemperaturaMinCelsius(r.temperaturaMinCelsius());
        a.setTemperaturaMaxCelsius(r.temperaturaMaxCelsius());
        a.setUmidadeMinPercentual(r.umidadeMinPercentual());
        a.setUmidadeMaxPercentual(r.umidadeMaxPercentual());
        a.setPressaoMinPa(r.pressaoMinPa());
        a.setPressaoMaxPa(r.pressaoMaxPa());
        if (r.ativo() != null) a.setAtivo(r.ativo());
        a.setObservacoes(r.observacoes());
    }

    private AmbienteDto toAmbienteDto(Ambiente a) {
        return new AmbienteDto(
                a.getId(), a.getTenant().getId(), a.getNome(), a.getTipoAmbiente(),
                a.getBloco(), a.getAndar(), a.getSetor(),
                a.getTemperaturaMinCelsius(), a.getTemperaturaMaxCelsius(),
                a.getUmidadeMinPercentual(), a.getUmidadeMaxPercentual(),
                a.getPressaoMinPa(), a.getPressaoMaxPa(),
                a.isAtivo(), a.getObservacoes());
    }

    // ============================================================
    // Monitoramento Ambiental (temperatura, umidade, pressão diferencial)
    // ============================================================

    public MonitoramentoAmbientalDto registrarMonitoramento(MonitoramentoAmbientalRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = loadTenant(request.tenantId());

        MonitoramentoAmbiental m = new MonitoramentoAmbiental();
        m.setTenant(tenant);
        m.setDataHora(request.dataHora());

        Ambiente ambiente = null;
        if (request.ambienteId() != null) {
            ambiente = ambienteRepository.findById(request.ambienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado"));
            m.setAmbiente(ambiente);
            m.setTipoAmbiente(ambiente.getTipoAmbiente());
        } else {
            m.setTipoAmbiente(request.tipoAmbiente());
        }

        m.setLocalSala(request.localSala());
        m.setTemperaturaCelsius(request.temperaturaCelsius());
        m.setUmidadeRelativa(request.umidadeRelativa());
        m.setPressaoDiferencialPa(request.pressaoDiferencialPa());

        ResultadoMonitoramento resultado = request.resultado();
        if (resultado == null) {
            resultado = avaliarResultadoAmbiental(
                    request.temperaturaCelsius(), request.umidadeRelativa(),
                    request.pressaoDiferencialPa(), ambiente);
        }
        m.setResultado(resultado);

        if (request.responsavelId() != null) m.setResponsavel(loadUser(request.responsavelId()));
        m.setObservacoes(request.observacoes());
        m.setEvidencias(loadEvidencias(request.evidenciasIds()));
        return toMonitoramentoDto(monitoramentoRepository.save(m));
    }

    @Transactional(readOnly = true)
    public Page<MonitoramentoAmbientalDto> listMonitoramentos(Pageable pageable) {
        return monitoramentoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toMonitoramentoDto);
    }

    @Transactional(readOnly = true)
    public MonitoramentoAmbientalDto findMonitoramentoById(Long id) {
        return monitoramentoRepository.findById(id)
                .map(this::toMonitoramentoDto)
                .orElseThrow(() -> new EntityNotFoundException("Monitoramento ambiental não encontrado"));
    }

    private MonitoramentoAmbientalDto toMonitoramentoDto(MonitoramentoAmbiental m) {
        Ambiente amb = m.getAmbiente();
        return new MonitoramentoAmbientalDto(
                m.getId(), m.getTenant().getId(), m.getDataHora(),
                amb != null ? amb.getId() : null,
                amb != null ? amb.getNome() : null,
                m.getTipoAmbiente(), m.getLocalSala(),
                m.getTemperaturaCelsius(), m.getUmidadeRelativa(), m.getPressaoDiferencialPa(),
                m.getResultado(),
                m.getResponsavel() != null ? m.getResponsavel().getId() : null,
                m.getObservacoes(), toIdSet(m.getEvidencias()));
    }

    // ============================================================
    // Geladeira de Medicamentos / Vacinas
    // ============================================================

    public GeladeiraMedicamentosDto cadastrarGeladeira(GeladeiraMedicamentosRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = loadTenant(request.tenantId());

        GeladeiraMedicamentos g = new GeladeiraMedicamentos();
        g.setTenant(tenant);
        applyGeladeiraFields(g, request);
        return toGeladeiraDto(geladeiraRepository.save(g));
    }

    public GeladeiraMedicamentosDto updateGeladeira(Long id, GeladeiraMedicamentosRequest request) {
        GeladeiraMedicamentos g = geladeiraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada"));
        tenantScopeGuard.checkRequestedTenant(g.getTenant().getId());
        applyGeladeiraFields(g, request);
        return toGeladeiraDto(geladeiraRepository.save(g));
    }

    public GeladeiraMedicamentosDto toggleGeladeiraStatus(Long id, boolean ativo) {
        GeladeiraMedicamentos g = geladeiraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada"));
        tenantScopeGuard.checkRequestedTenant(g.getTenant().getId());
        g.setAtivo(ativo);
        return toGeladeiraDto(geladeiraRepository.save(g));
    }

    @Transactional(readOnly = true)
    public Page<GeladeiraMedicamentosDto> listGeladeiras(Pageable pageable) {
        return geladeiraRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toGeladeiraDto);
    }

    @Transactional(readOnly = true)
    public GeladeiraMedicamentosDto findGeladeiraById(Long id) {
        return geladeiraRepository.findById(id)
                .map(this::toGeladeiraDto)
                .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada"));
    }

    private void applyGeladeiraFields(GeladeiraMedicamentos g, GeladeiraMedicamentosRequest r) {
        g.setNome(r.nome());
        g.setTipoUso(r.tipoUso());
        g.setLocalSala(r.localSala());
        g.setFabricante(r.fabricante());
        g.setModelo(r.modelo());
        g.setNumeroSerie(r.numeroSerie());
        g.setTemperaturaMinCelsius(r.temperaturaMinCelsius());
        g.setTemperaturaMaxCelsius(r.temperaturaMaxCelsius());
        g.setFrequenciaLeituraHoras(r.frequenciaLeituraHoras());
        if (r.ativo() != null) g.setAtivo(r.ativo());
        g.setObservacoes(r.observacoes());
    }

    private GeladeiraMedicamentosDto toGeladeiraDto(GeladeiraMedicamentos g) {
        return new GeladeiraMedicamentosDto(
                g.getId(), g.getTenant().getId(), g.getNome(), g.getTipoUso(),
                g.getLocalSala(), g.getFabricante(), g.getModelo(), g.getNumeroSerie(),
                g.getTemperaturaMinCelsius(), g.getTemperaturaMaxCelsius(),
                g.getFrequenciaLeituraHoras(), g.isAtivo(), g.getObservacoes());
    }

    // ============================================================
    // Registro de Temperatura de Geladeira
    // ============================================================

    public RegistroTemperaturaGeladeiraDto registrarTemperatura(RegistroTemperaturaGeladeiraRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = loadTenant(request.tenantId());
        GeladeiraMedicamentos geladeira = geladeiraRepository.findById(request.geladeiraId())
                .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada"));

        RegistroTemperaturaGeladeira r = buildRegistroTemperatura(tenant, geladeira,
                request.dataHora(), request.temperaturaCelsius(), request.umidadeRelativa(),
                request.resultado(), request.acaoCorretiva(),
                request.responsavelId() != null ? loadUser(request.responsavelId()) : null,
                request.observacoes());
        return toRegistroDto(registroRepository.save(r));
    }

    @Transactional(readOnly = true)
    public Page<RegistroTemperaturaGeladeiraDto> listRegistros(Long geladeiraId, Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Page<RegistroTemperaturaGeladeira> page = geladeiraId != null
                ? registroRepository.findAllByTenantIdAndGeladeiraId(tenantId, geladeiraId, pageable)
                : registroRepository.findAllByTenantId(tenantId, pageable);
        return page.map(this::toRegistroDto);
    }

    @Transactional(readOnly = true)
    public RegistroTemperaturaGeladeiraDto findRegistroById(Long id) {
        return registroRepository.findById(id)
                .map(this::toRegistroDto)
                .orElseThrow(() -> new EntityNotFoundException("Registro de temperatura não encontrado"));
    }

    private RegistroTemperaturaGeladeira buildRegistroTemperatura(
            Tenant tenant, GeladeiraMedicamentos geladeira,
            LocalDateTime dataHora, Double tempC, Double umidade,
            ResultadoMonitoramento resultadoOverride,
            String acaoCorretiva, User responsavel, String observacoes) {

        RegistroTemperaturaGeladeira r = new RegistroTemperaturaGeladeira();
        r.setTenant(tenant);
        r.setGeladeira(geladeira);
        r.setDataHora(dataHora != null ? dataHora : LocalDateTime.now());
        r.setTemperaturaCelsius(tempC != null ? tempC : 0.0);
        r.setUmidadeRelativa(umidade);
        r.setResultado(resultadoOverride != null ? resultadoOverride
                : avaliarResultadoGeladeira(tempC != null ? tempC : 0.0, geladeira));
        r.setAcaoCorretiva(acaoCorretiva);
        r.setResponsavel(responsavel);
        r.setObservacoes(observacoes);
        return r;
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

    // ============================================================
    // Dispositivos IoT
    // ============================================================

    public DispositivoIoTDto cadastrarDispositivo(DispositivoIoTRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = loadTenant(request.tenantId());
        validarVinculoDispositivo(request);

        DispositivoIoT d = new DispositivoIoT();
        d.setTenant(tenant);
        d.setDeviceId(request.deviceId());
        d.setTipo(request.tipo());
        d.setApiKey(UUID.randomUUID().toString().replace("-", ""));
        d.setAtivo(request.ativo() != null ? request.ativo() : true);
        d.setDescricao(request.descricao());
        d.setLocalInstalacao(request.localInstalacao());

        if (request.geladeiraId() != null) {
            d.setGeladeira(geladeiraRepository.findById(request.geladeiraId())
                    .orElseThrow(() -> new EntityNotFoundException("Geladeira não encontrada")));
        }
        if (request.ambienteId() != null) {
            d.setAmbiente(ambienteRepository.findById(request.ambienteId())
                    .orElseThrow(() -> new EntityNotFoundException("Ambiente não encontrado")));
        }
        return toDispositivoDto(dispositivoRepository.save(d));
    }

    public DispositivoIoTDto toggleDispositivoStatus(Long id, boolean ativo) {
        DispositivoIoT d = dispositivoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo IoT não encontrado"));
        tenantScopeGuard.checkRequestedTenant(d.getTenant().getId());
        d.setAtivo(ativo);
        return toDispositivoDto(dispositivoRepository.save(d));
    }

    public DispositivoIoTDto regenerarApiKey(Long id) {
        DispositivoIoT d = dispositivoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo IoT não encontrado"));
        tenantScopeGuard.checkRequestedTenant(d.getTenant().getId());
        d.setApiKey(UUID.randomUUID().toString().replace("-", ""));
        return toDispositivoDto(dispositivoRepository.save(d));
    }

    @Transactional(readOnly = true)
    public Page<DispositivoIoTDto> listDispositivos(Pageable pageable) {
        return dispositivoRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(this::toDispositivoDto);
    }

    @Transactional(readOnly = true)
    public DispositivoIoTDto findDispositivoById(Long id) {
        return dispositivoRepository.findById(id)
                .map(this::toDispositivoDto)
                .orElseThrow(() -> new EntityNotFoundException("Dispositivo IoT não encontrado"));
    }

    private void validarVinculoDispositivo(DispositivoIoTRequest r) {
        if (r.tipo() == TipoDispositivoIoT.TEMPERATURA_GELADEIRA && r.geladeiraId() == null) {
            throw new IllegalArgumentException("geladeiraId é obrigatório para dispositivos do tipo TEMPERATURA_GELADEIRA");
        }
        if (r.tipo() == TipoDispositivoIoT.MONITORAMENTO_AMBIENTAL && r.ambienteId() == null) {
            throw new IllegalArgumentException("ambienteId é obrigatório para dispositivos do tipo MONITORAMENTO_AMBIENTAL");
        }
    }

    private DispositivoIoTDto toDispositivoDto(DispositivoIoT d) {
        GeladeiraMedicamentos gel = d.getGeladeira();
        Ambiente amb = d.getAmbiente();
        return new DispositivoIoTDto(
                d.getId(), d.getTenant().getId(), d.getDeviceId(), d.getTipo(), d.getApiKey(),
                gel != null ? gel.getId() : null, gel != null ? gel.getNome() : null,
                amb != null ? amb.getId() : null, amb != null ? amb.getNome() : null,
                d.isAtivo(), d.getDescricao(), d.getLocalInstalacao(), d.getUltimaLeitura());
    }

    // ============================================================
    // IoT — processamento de leitura recebida via X-Device-Key
    // ============================================================

    /**
     * Processa uma leitura enviada por um dispositivo IoT autenticado.
     * Roteia para {@link RegistroTemperaturaGeladeira} ou {@link MonitoramentoAmbiental}
     * de acordo com o tipo do dispositivo. Atualiza {@code ultimaLeitura} do dispositivo.
     */
    public IoTLeituraResponse processarLeituraIoT(DispositivoIoT dispositivo, IoTLeituraRequest leitura) {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime dataHora = leitura.dataHora() != null ? leitura.dataHora() : agora;

        // Atualiza timestamp de última leitura
        dispositivo.setUltimaLeitura(agora);
        dispositivoRepository.save(dispositivo);

        return switch (dispositivo.getTipo()) {
            case TEMPERATURA_GELADEIRA -> processarLeituraGeladeira(dispositivo, leitura, dataHora);
            case MONITORAMENTO_AMBIENTAL -> processarLeituraAmbiental(dispositivo, leitura, dataHora);
        };
    }

    private IoTLeituraResponse processarLeituraGeladeira(DispositivoIoT d, IoTLeituraRequest leitura,
                                                          LocalDateTime dataHora) {
        if (leitura.temperaturaC() == null) {
            throw new IllegalArgumentException("temperaturaC é obrigatório para dispositivos de geladeira");
        }
        GeladeiraMedicamentos geladeira = d.getGeladeira();
        if (geladeira == null) {
            throw new IllegalStateException("Dispositivo " + d.getDeviceId() + " não está vinculado a uma geladeira");
        }

        RegistroTemperaturaGeladeira r = buildRegistroTemperatura(
                geladeira.getTenant(), geladeira,
                dataHora, leitura.temperaturaC(), leitura.umidade(),
                null, null, null, "Leitura automática IoT — " + d.getDeviceId());

        RegistroTemperaturaGeladeira saved = registroRepository.save(r);
        return new IoTLeituraResponse(TipoDispositivoIoT.TEMPERATURA_GELADEIRA,
                saved.getId(), saved.getResultado(), LocalDateTime.now());
    }

    private IoTLeituraResponse processarLeituraAmbiental(DispositivoIoT d, IoTLeituraRequest leitura,
                                                          LocalDateTime dataHora) {
        Ambiente ambiente = d.getAmbiente();
        if (ambiente == null) {
            throw new IllegalStateException("Dispositivo " + d.getDeviceId() + " não está vinculado a um ambiente");
        }

        MonitoramentoAmbiental m = new MonitoramentoAmbiental();
        m.setTenant(ambiente.getTenant());
        m.setDataHora(dataHora);
        m.setAmbiente(ambiente);
        m.setTipoAmbiente(ambiente.getTipoAmbiente());
        m.setTemperaturaCelsius(leitura.temperaturaC());
        m.setUmidadeRelativa(leitura.umidade());
        m.setPressaoDiferencialPa(leitura.pressaoPa());
        m.setResultado(avaliarResultadoAmbiental(
                leitura.temperaturaC(), leitura.umidade(), leitura.pressaoPa(), ambiente));
        m.setObservacoes("Leitura automática IoT — " + d.getDeviceId());

        MonitoramentoAmbiental saved = monitoramentoRepository.save(m);
        return new IoTLeituraResponse(TipoDispositivoIoT.MONITORAMENTO_AMBIENTAL,
                saved.getId(), saved.getResultado(), LocalDateTime.now());
    }

    // ============================================================
    // Avaliação automática de conformidade
    // ============================================================

    /**
     * Avalia o resultado de temperatura de geladeira com margem de alerta de 10% da faixa.
     */
    private ResultadoMonitoramento avaliarResultadoGeladeira(double temp, GeladeiraMedicamentos geladeira) {
        Double min = geladeira.getTemperaturaMinCelsius();
        Double max = geladeira.getTemperaturaMaxCelsius();
        if (min == null || max == null) return ResultadoMonitoramento.CONFORME;

        double faixa = max - min;
        double margem = faixa * 0.10;

        if (temp < min || temp > max) return ResultadoMonitoramento.NAO_CONFORME;
        if (temp < (min + margem) || temp > (max - margem)) return ResultadoMonitoramento.ALERTA;
        return ResultadoMonitoramento.CONFORME;
    }

    /**
     * Avalia o resultado de monitoramento ambiental comparando temperatura, umidade
     * e pressão diferencial com os parâmetros alvo do ambiente cadastrado.
     * Qualquer parâmetro fora do range resulta em NAO_CONFORME.
     * Qualquer parâmetro dentro de 10% do limite resulta em ALERTA.
     * Se o ambiente não tem parâmetros configurados, retorna CONFORME.
     */
    private ResultadoMonitoramento avaliarResultadoAmbiental(Double temp, Double umidade,
                                                              Double pressao, Ambiente ambiente) {
        if (ambiente == null) return ResultadoMonitoramento.CONFORME;

        ResultadoMonitoramento pior = ResultadoMonitoramento.CONFORME;

        pior = pior(pior, avaliarParametro(temp, ambiente.getTemperaturaMinCelsius(), ambiente.getTemperaturaMaxCelsius()));
        pior = pior(pior, avaliarParametro(umidade, ambiente.getUmidadeMinPercentual(), ambiente.getUmidadeMaxPercentual()));
        pior = pior(pior, avaliarParametro(pressao, ambiente.getPressaoMinPa(), ambiente.getPressaoMaxPa()));

        return pior;
    }

    private ResultadoMonitoramento avaliarParametro(Double valor, Double min, Double max) {
        if (valor == null || (min == null && max == null)) return ResultadoMonitoramento.CONFORME;
        if (min != null && max != null) {
            double faixa = max - min;
            double margem = faixa * 0.10;
            if (valor < min || valor > max) return ResultadoMonitoramento.NAO_CONFORME;
            if (valor < (min + margem) || valor > (max - margem)) return ResultadoMonitoramento.ALERTA;
        } else if (min != null && valor < min) {
            return ResultadoMonitoramento.NAO_CONFORME;
        } else if (max != null && valor > max) {
            return ResultadoMonitoramento.NAO_CONFORME;
        }
        return ResultadoMonitoramento.CONFORME;
    }

    /** Retorna o resultado mais grave entre dois. */
    private ResultadoMonitoramento pior(ResultadoMonitoramento a, ResultadoMonitoramento b) {
        if (a == ResultadoMonitoramento.NAO_CONFORME || b == ResultadoMonitoramento.NAO_CONFORME)
            return ResultadoMonitoramento.NAO_CONFORME;
        if (a == ResultadoMonitoramento.ALERTA || b == ResultadoMonitoramento.ALERTA)
            return ResultadoMonitoramento.ALERTA;
        return ResultadoMonitoramento.CONFORME;
    }

    // ============================================================
    // Dashboard
    // ============================================================

    @Transactional(readOnly = true)
    public EnvironmentalDashboardDto getDashboard() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        LocalDateTime agora      = LocalDateTime.now();
        LocalDateTime h24Atras   = agora.minusHours(24);
        LocalDateTime h2Atras    = agora.minusHours(2);

        // ── Totais de cadastro ───────────────────────────────────────────────
        long totalAmbientes   = ambienteRepository.countByTenantIdAndAtivo(tenantId, true);
        long totalGeladeiras  = geladeiraRepository.countByTenantIdAndAtivo(tenantId, true);
        long totalDispositivos = dispositivoRepository.countByTenantIdAndAtivo(tenantId, true);
        long dispositivosOffline = dispositivoRepository.countOffline(tenantId, h2Atras);

        // ── Status atual por ambiente (última leitura de cada sala) ──────────
        List<Ambiente> ambientesAtivos = ambienteRepository.findAllByTenantIdAndAtivo(tenantId, true);
        List<MonitoramentoAmbiental> ultimasMonitoramentos = monitoramentoRepository.findUltimaLeituraPorAmbiente(tenantId);

        Set<Long> ambientesComLeitura = ultimasMonitoramentos.stream()
                .map(m -> m.getAmbiente().getId())
                .collect(Collectors.toSet());

        long ambStatusConforme    = contarPorResultado(ultimasMonitoramentos, ResultadoMonitoramento.CONFORME);
        long ambStatusAlerta      = contarPorResultado(ultimasMonitoramentos, ResultadoMonitoramento.ALERTA);
        long ambStatusNaoConforme = contarPorResultado(ultimasMonitoramentos, ResultadoMonitoramento.NAO_CONFORME);
        long ambSemLeitura        = ambientesAtivos.stream()
                .filter(a -> !ambientesComLeitura.contains(a.getId()))
                .count();

        // ── Status atual por geladeira (última leitura de cada geladeira) ────
        List<GeladeiraMedicamentos> geladeiraAtivas = geladeiraRepository.findAllByTenantIdAndAtivo(tenantId, true);
        List<RegistroTemperaturaGeladeira> ultimosRegistros = registroRepository.findUltimaLeituraPorGeladeira(tenantId);

        Set<Long> geladeiraComLeitura = ultimosRegistros.stream()
                .map(r -> r.getGeladeira().getId())
                .collect(Collectors.toSet());

        long gelStatusConforme    = contarRegistroPorResultado(ultimosRegistros, ResultadoMonitoramento.CONFORME);
        long gelStatusAlerta      = contarRegistroPorResultado(ultimosRegistros, ResultadoMonitoramento.ALERTA);
        long gelStatusNaoConforme = contarRegistroPorResultado(ultimosRegistros, ResultadoMonitoramento.NAO_CONFORME);
        long gelSemLeitura        = geladeiraAtivas.stream()
                .filter(g -> !geladeiraComLeitura.contains(g.getId()))
                .count();

        // ── Leituras das últimas 24h — ambientes ─────────────────────────────
        long mon24Conforme    = monitoramentoRepository.countByTenantIdAndResultadoAndDataHoraAfter(tenantId, ResultadoMonitoramento.CONFORME,    h24Atras);
        long mon24Alerta      = monitoramentoRepository.countByTenantIdAndResultadoAndDataHoraAfter(tenantId, ResultadoMonitoramento.ALERTA,       h24Atras);
        long mon24NaoConforme = monitoramentoRepository.countByTenantIdAndResultadoAndDataHoraAfter(tenantId, ResultadoMonitoramento.NAO_CONFORME, h24Atras);

        // ── Leituras das últimas 24h — geladeiras ────────────────────────────
        long reg24Conforme    = registroRepository.countByTenantIdAndResultadoAndDataHoraAfter(tenantId, ResultadoMonitoramento.CONFORME,    h24Atras);
        long reg24Alerta      = registroRepository.countByTenantIdAndResultadoAndDataHoraAfter(tenantId, ResultadoMonitoramento.ALERTA,       h24Atras);
        long reg24NaoConforme = registroRepository.countByTenantIdAndResultadoAndDataHoraAfter(tenantId, ResultadoMonitoramento.NAO_CONFORME, h24Atras);

        // ── Alertas ativos (top 20, mais recentes primeiro) ──────────────────
        var resultadosAlerta = List.of(ResultadoMonitoramento.ALERTA, ResultadoMonitoramento.NAO_CONFORME);
        Pageable top20 = PageRequest.of(0, 20);

        List<AlertaAmbientalDto> alertasAmbientes = monitoramentoRepository
                .findAlertasRecentes(tenantId, resultadosAlerta, h24Atras, top20)
                .stream()
                .map(m -> new AlertaAmbientalDto(
                        "AMBIENTE",
                        m.getAmbiente() != null ? m.getAmbiente().getId() : null,
                        m.getAmbiente() != null ? m.getAmbiente().getNome() : m.getLocalSala(),
                        m.getResultado(),
                        m.getTemperaturaCelsius(),
                        m.getUmidadeRelativa(),
                        m.getPressaoDiferencialPa(),
                        m.getDataHora()))
                .toList();

        List<AlertaAmbientalDto> alertasGeladeiras = registroRepository
                .findAlertasRecentes(tenantId, resultadosAlerta, h24Atras, top20)
                .stream()
                .map(r -> new AlertaAmbientalDto(
                        "GELADEIRA",
                        r.getGeladeira().getId(),
                        r.getGeladeira().getNome(),
                        r.getResultado(),
                        r.getTemperaturaCelsius(),
                        r.getUmidadeRelativa(),
                        null,
                        r.getDataHora()))
                .toList();

        // Intercala ambientes e geladeiras, ordena por dataHora desc, limita em 20
        List<AlertaAmbientalDto> alertasAtivos = Stream
                .concat(alertasAmbientes.stream(), alertasGeladeiras.stream())
                .sorted(Comparator.comparing(AlertaAmbientalDto::dataHora).reversed())
                .limit(20)
                .toList();

        return new EnvironmentalDashboardDto(
                totalAmbientes, totalGeladeiras, totalDispositivos, dispositivosOffline,
                ambStatusConforme, ambStatusAlerta, ambStatusNaoConforme, ambSemLeitura,
                gelStatusConforme, gelStatusAlerta, gelStatusNaoConforme, gelSemLeitura,
                mon24Conforme, mon24Alerta, mon24NaoConforme, mon24Conforme + mon24Alerta + mon24NaoConforme,
                reg24Conforme, reg24Alerta, reg24NaoConforme, reg24Conforme + reg24Alerta + reg24NaoConforme,
                alertasAtivos,
                agora
        );
    }

    private long contarPorResultado(List<MonitoramentoAmbiental> lista, ResultadoMonitoramento resultado) {
        return lista.stream().filter(m -> resultado == m.getResultado()).count();
    }

    private long contarRegistroPorResultado(List<RegistroTemperaturaGeladeira> lista, ResultadoMonitoramento resultado) {
        return lista.stream().filter(r -> resultado == r.getResultado()).count();
    }

    // ============================================================
    // Helpers
    // ============================================================

    private Tenant loadTenant(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
    }

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
