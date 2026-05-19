package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.CmeDashboardDto;
import com.erp.qualitascareapi.cme.enums.CicloStatus;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import com.erp.qualitascareapi.cme.repo.CicloEsterilizacaoRepository;
import com.erp.qualitascareapi.cme.repo.LoteEtiquetaRepository;
import com.erp.qualitascareapi.cme.repo.ProcessoReprocessamentoRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CmeDashboardService {

    private static final List<CicloStatus> CICLO_SUCESSO = List.of(CicloStatus.CONCLUIDO, CicloStatus.LIBERADO);
    private static final List<LoteStatus> LOTE_PENDENTE = List.of(LoteStatus.MONTADO, LoteStatus.EM_PROCESSO);
    private static final List<ProcessoStatus> PROCESSO_TERMINAL = List.of(
            ProcessoStatus.LIBERADO, ProcessoStatus.REPROVADO, ProcessoStatus.CANCELADO);

    private final CicloEsterilizacaoRepository cicloRepository;
    private final LoteEtiquetaRepository loteRepository;
    private final ProcessoReprocessamentoRepository processoRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public CmeDashboardService(CicloEsterilizacaoRepository cicloRepository,
                               LoteEtiquetaRepository loteRepository,
                               ProcessoReprocessamentoRepository processoRepository,
                               TenantScopeGuard tenantScopeGuard) {
        this.cicloRepository = cicloRepository;
        this.loteRepository = loteRepository;
        this.processoRepository = processoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public CmeDashboardDto getDashboard() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (tenantId == null) {
            throw new AccessDeniedException("Tenant context is required");
        }
        LocalDateTime since = LocalDateTime.now().minusDays(7);

        long totalCiclos = cicloRepository.countByTenant_IdAndInicioAfter(tenantId, since);
        long ciclosSucesso = cicloRepository.countByTenant_IdAndStatusInAndInicioAfter(tenantId, CICLO_SUCESSO, since);
        double sterilizationRate = totalCiclos > 0 ? (double) ciclosSucesso / totalCiclos : 0.0;

        Double avgMinutos = cicloRepository.avgDuracaoMinutosByTenantAndInicioAfter(tenantId, since);
        double turnaroundTimeMinutes = avgMinutos != null ? avgMinutos : 0.0;

        long pendingLoads = loteRepository.countByTenant_IdAndStatusIn(tenantId, LOTE_PENDENTE);
        long processosAbertos = processoRepository.countByTenant_IdAndStatusNotIn(tenantId, PROCESSO_TERMINAL);

        return new CmeDashboardDto(sterilizationRate, turnaroundTimeMinutes, pendingLoads, processosAbertos);
    }
}
