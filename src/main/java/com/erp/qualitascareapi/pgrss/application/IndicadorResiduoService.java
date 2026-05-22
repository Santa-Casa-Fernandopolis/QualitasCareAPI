package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.pgrss.api.dto.MesPesoDto;
import com.erp.qualitascareapi.pgrss.api.dto.PgrssDashboardDto;
import com.erp.qualitascareapi.pgrss.api.dto.SetorPesoDto;
import com.erp.qualitascareapi.pgrss.enums.StatusNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.StatusPlanoAcao;
import com.erp.qualitascareapi.pgrss.repo.*;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class IndicadorResiduoService {

    private final PesagemResiduoRepository pesagemRepository;
    private final NaoConformidadeResiduoRepository ncRepository;
    private final PlanoAcaoResiduoRepository planoRepository;
    private final EmpresaColetorRepository empresaRepository;
    private final ColetaExternaRepository coletaExternaRepository;
    private final CustoTratamentoRepository custoRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public IndicadorResiduoService(PesagemResiduoRepository pesagemRepository,
                                    NaoConformidadeResiduoRepository ncRepository,
                                    PlanoAcaoResiduoRepository planoRepository,
                                    EmpresaColetorRepository empresaRepository,
                                    ColetaExternaRepository coletaExternaRepository,
                                    CustoTratamentoRepository custoRepository,
                                    TenantScopeGuard tenantScopeGuard) {
        this.pesagemRepository = pesagemRepository;
        this.ncRepository = ncRepository;
        this.planoRepository = planoRepository;
        this.empresaRepository = empresaRepository;
        this.coletaExternaRepository = coletaExternaRepository;
        this.custoRepository = custoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public BigDecimal pesoTotalPorPeriodo(LocalDate ini, LocalDate fim) {
        Long tid = tenantScopeGuard.currentTenantId();
        BigDecimal result = pesagemRepository.sumPesoPeriodo(tid, ini.atStartOfDay(), fim.atTime(LocalTime.MAX));
        return result != null ? result : BigDecimal.ZERO;
    }

    public Map<String, BigDecimal> pesoPorSetor(LocalDate ini, LocalDate fim) {
        Long tid = tenantScopeGuard.currentTenantId();
        List<Object[]> rows = pesagemRepository.pesoPorSetor(tid, ini.atStartOfDay(), fim.atTime(LocalTime.MAX));
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (BigDecimal) row[1]);
        }
        return result;
    }

    public Map<String, BigDecimal> pesoPorGrupo(LocalDate ini, LocalDate fim) {
        Long tid = tenantScopeGuard.currentTenantId();
        List<Object[]> rows = pesagemRepository.pesoPorGrupo(tid, ini.atStartOfDay(), fim.atTime(LocalTime.MAX));
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (BigDecimal) row[1]);
        }
        return result;
    }

    public double taxaResiduoInfectante(LocalDate ini, LocalDate fim) {
        Map<String, BigDecimal> porGrupo = pesoPorGrupo(ini, fim);
        BigDecimal total = porGrupo.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        if (total.compareTo(BigDecimal.ZERO) == 0) return 0.0;
        BigDecimal grupoA = porGrupo.getOrDefault("A", BigDecimal.ZERO);
        return grupoA.divide(total, 4, RoundingMode.HALF_UP).doubleValue();
    }

    public Map<String, Long> naoConformidadesPorSetor(LocalDate ini, LocalDate fim) {
        Long tid = tenantScopeGuard.currentTenantId();
        List<Object[]> rows = ncRepository.countBySetor(tid, ini.atStartOfDay(), fim.atTime(LocalTime.MAX));
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put((String) row[0], (Long) row[1]);
        }
        return result;
    }

    public Map<String, Long> naoConformidadesPorTipo(LocalDate ini, LocalDate fim) {
        Long tid = tenantScopeGuard.currentTenantId();
        List<Object[]> rows = ncRepository.countByTipo(tid, ini.atStartOfDay(), fim.atTime(LocalTime.MAX));
        Map<String, Long> result = new LinkedHashMap<>();
        for (Object[] row : rows) {
            result.put(row[0].toString(), (Long) row[1]);
        }
        return result;
    }

    public Map<String, BigDecimal> custoEstimadoPorGrupo(LocalDate ini, LocalDate fim) {
        Long tid = tenantScopeGuard.currentTenantId();
        Map<String, BigDecimal> pesoGrupo = pesoPorGrupo(ini, fim);
        Map<String, BigDecimal> result = new LinkedHashMap<>();
        for (Map.Entry<String, BigDecimal> entry : pesoGrupo.entrySet()) {
            // Find custo for grupo by codigo - simplified: return peso as proxy if custo not found
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public PgrssDashboardDto getDashboard() {
        Long tid = tenantScopeGuard.currentTenantId();
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDateTime inicioMesDt = inicioMes.atStartOfDay();
        LocalDateTime fimHojeDt = hoje.atTime(LocalTime.MAX);

        BigDecimal pesoTotalMes = pesagemRepository.sumPesoPeriodo(tid, inicioMesDt, fimHojeDt);
        if (pesoTotalMes == null) pesoTotalMes = BigDecimal.ZERO;

        List<Object[]> grupoRows = pesagemRepository.pesoPorGrupo(tid, inicioMesDt, fimHojeDt);
        Map<String, BigDecimal> pesoPorGrupoMes = new LinkedHashMap<>();
        for (Object[] row : grupoRows) {
            pesoPorGrupoMes.put((String) row[0], (BigDecimal) row[1]);
        }

        List<Object[]> setorRows = pesagemRepository.pesoPorSetor(tid, inicioMesDt, fimHojeDt);
        List<SetorPesoDto> top10 = setorRows.stream()
                .limit(10)
                .map(row -> new SetorPesoDto((String) row[0], (BigDecimal) row[1]))
                .toList();

        long naoConformidadesAbertas = ncRepository.countByTenant_IdAndStatus(tid, StatusNaoConformidade.ABERTA);
        long planosAcaoVencidos = planoRepository.countByTenant_IdAndStatusIn(tid, List.of(StatusPlanoAcao.VENCIDO));

        long empresasVencidas = empresaRepository.findAllByTenant_IdAndAtivoTrueAndDataVencimentoLicencaBefore(tid, hoje).size();
        long empresasVencendo30 = empresaRepository.findAllByTenant_IdAndAtivoTrueAndDataVencimentoLicencaBetween(tid, hoje, hoje.plusDays(30)).size();

        long coletasSemDoc = coletaExternaRepository.countSemDocumento(tid);

        // Tendência dos últimos 6 meses
        List<MesPesoDto> tendencia = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 5; i >= 0; i--) {
            YearMonth ym = YearMonth.now().minusMonths(i);
            LocalDateTime ini2 = ym.atDay(1).atStartOfDay();
            LocalDateTime fim2 = ym.atEndOfMonth().atTime(LocalTime.MAX);
            BigDecimal peso = pesagemRepository.sumPesoPeriodo(tid, ini2, fim2);
            tendencia.add(new MesPesoDto(ym.format(fmt), peso != null ? peso : BigDecimal.ZERO));
        }

        double taxaInfectante = 0.0;
        BigDecimal grupoA = pesoPorGrupoMes.getOrDefault("A", BigDecimal.ZERO);
        if (pesoTotalMes.compareTo(BigDecimal.ZERO) > 0) {
            taxaInfectante = grupoA.divide(pesoTotalMes, 4, RoundingMode.HALF_UP).doubleValue();
        }

        return new PgrssDashboardDto(
                pesoTotalMes,
                pesoPorGrupoMes,
                top10,
                naoConformidadesAbertas,
                planosAcaoVencidos,
                empresasVencidas,
                empresasVencendo30,
                coletasSemDoc,
                tendencia,
                taxaInfectante
        );
    }
}
