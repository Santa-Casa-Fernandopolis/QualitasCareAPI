package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.CmeDashboardDto;
import com.erp.qualitascareapi.cme.api.dto.RecebimentoMaterialDto;
import com.erp.qualitascareapi.cme.domain.RecebimentoMaterial;
import com.erp.qualitascareapi.cme.enums.CicloStatus;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.cme.enums.ManutencaoStatus;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import com.erp.qualitascareapi.cme.repo.CicloEsterilizacaoRepository;
import com.erp.qualitascareapi.cme.repo.LoteEtiquetaRepository;
import com.erp.qualitascareapi.cme.repo.ManutencaoAutoclaveRepository;
import com.erp.qualitascareapi.cme.repo.ProcessoReprocessamentoRepository;
import com.erp.qualitascareapi.cme.repo.RecebimentoMaterialRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CmeDashboardService {

    private static final List<CicloStatus> CICLO_SUCESSO =
            List.of(CicloStatus.CONCLUIDO, CicloStatus.LIBERADO);

    private static final List<LoteStatus> LOTE_PENDENTE =
            List.of(LoteStatus.MONTADO, LoteStatus.EM_PROCESSO);

    private static final List<LoteStatus> LOTE_ATIVO =
            List.of(LoteStatus.MONTADO, LoteStatus.EM_PROCESSO, LoteStatus.LIBERADO,
                    LoteStatus.DISPONIVEL_ESTOQUE, LoteStatus.BLOQUEADO);

    private static final List<ProcessoStatus> PROCESSO_TERMINAL =
            List.of(ProcessoStatus.LIBERADO, ProcessoStatus.REPROVADO, ProcessoStatus.CANCELADO);

    private static final List<ManutencaoStatus> MANUTENCAO_PENDENTE =
            List.of(ManutencaoStatus.PLANEJADA, ManutencaoStatus.ABERTA, ManutencaoStatus.EM_ANDAMENTO);

    private final CicloEsterilizacaoRepository cicloRepository;
    private final LoteEtiquetaRepository loteRepository;
    private final ProcessoReprocessamentoRepository processoRepository;
    private final RecebimentoMaterialRepository recebimentoRepository;
    private final ManutencaoAutoclaveRepository manutencaoRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public CmeDashboardService(CicloEsterilizacaoRepository cicloRepository,
                               LoteEtiquetaRepository loteRepository,
                               ProcessoReprocessamentoRepository processoRepository,
                               RecebimentoMaterialRepository recebimentoRepository,
                               ManutencaoAutoclaveRepository manutencaoRepository,
                               TenantScopeGuard tenantScopeGuard) {
        this.cicloRepository = cicloRepository;
        this.loteRepository = loteRepository;
        this.processoRepository = processoRepository;
        this.recebimentoRepository = recebimentoRepository;
        this.manutencaoRepository = manutencaoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public CmeDashboardDto getDashboard() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (tenantId == null) {
            throw new AccessDeniedException("Tenant context is required");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime since7d = now.minusDays(7);
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDate today = now.toLocalDate();
        LocalDate in30Days = today.plusDays(30);

        // KPIs — esterilização
        long totalCiclos = cicloRepository.countByTenant_IdAndInicioAfter(tenantId, since7d);
        long ciclosSucesso = cicloRepository.countByTenant_IdAndStatusInAndInicioAfter(tenantId, CICLO_SUCESSO, since7d);
        double sterilizationRate = totalCiclos > 0 ? (double) ciclosSucesso / totalCiclos : 0.0;

        Double avgMinutos = cicloRepository.avgDuracaoMinutosByTenantAndInicioAfter(tenantId, since7d);
        double turnaroundTimeMinutes = avgMinutos != null ? avgMinutos : 0.0;

        long pendingLoads = loteRepository.countByTenant_IdAndStatusIn(tenantId, LOTE_PENDENTE);
        long processosAbertos = processoRepository.countByTenant_IdAndStatusNotIn(tenantId, PROCESSO_TERMINAL);

        // KPIs — operacionais
        long recebimentosHoje = recebimentoRepository.countByTenant_IdAndDataHoraAfter(tenantId, todayStart);
        long ciclosHoje = cicloRepository.countByTenant_IdAndInicioAfter(tenantId, todayStart);
        long lotesVencendoEm30Dias = loteRepository.countByTenant_IdAndValidadeBetweenAndStatusIn(tenantId, today, in30Days, LOTE_ATIVO);
        long manutencoesPendentes = manutencaoRepository.countByAutoclave_TenantIdAndStatusIn(tenantId, MANUTENCAO_PENDENTE);

        // Distribuições
        Map<String, Long> processosPorStatus = toMap(processoRepository.groupByStatusForTenant(tenantId));
        Map<String, Long> ciclosPorStatus = toMap(cicloRepository.groupByStatusForTenant(tenantId));
        Map<String, Long> lotesPorStatus = toMap(loteRepository.groupByStatusForTenant(tenantId));

        // Recentes
        var pageable = PageRequest.of(0, 5, Sort.by("dataHora").descending());
        List<RecebimentoMaterialDto> recebimentosRecentes = recebimentoRepository
                .findAllByTenantId(tenantId, pageable)
                .getContent()
                .stream()
                .map(this::toRecebimentoDto)
                .toList();

        return new CmeDashboardDto(
                sterilizationRate, turnaroundTimeMinutes, pendingLoads, processosAbertos,
                recebimentosHoje, ciclosHoje, lotesVencendoEm30Dias, manutencoesPendentes,
                processosPorStatus, ciclosPorStatus, lotesPorStatus,
                recebimentosRecentes
        );
    }

    private Map<String, Long> toMap(List<Object[]> rows) {
        Map<String, Long> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put(row[0].toString(), (Long) row[1]);
        }
        return map;
    }

    private RecebimentoMaterialDto toRecebimentoDto(RecebimentoMaterial r) {
        return new RecebimentoMaterialDto(
                r.getId(),
                r.getTenant().getId(),
                r.getDataHora(),
                r.getSetorOrigem() != null ? r.getSetorOrigem().getId() : null,
                r.getResponsavel() != null ? r.getResponsavel().getId() : null,
                r.getQuantidadeItens(),
                r.getCondicaoDescricao(),
                r.getStatus(),
                r.getObservacoes(),
                Set.of()
        );
    }
}
