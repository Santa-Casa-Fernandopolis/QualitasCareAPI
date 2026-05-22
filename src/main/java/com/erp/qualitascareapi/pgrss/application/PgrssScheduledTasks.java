package com.erp.qualitascareapi.pgrss.application;

import com.erp.qualitascareapi.notificacao.application.NotificacaoService;
import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import com.erp.qualitascareapi.pgrss.domain.EmpresaColetora;
import com.erp.qualitascareapi.pgrss.domain.PlanoAcaoResiduo;
import com.erp.qualitascareapi.pgrss.enums.StatusPlanoAcao;
import com.erp.qualitascareapi.pgrss.repo.EmpresaColetorRepository;
import com.erp.qualitascareapi.pgrss.repo.PlanoAcaoResiduoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Tarefas agendadas do módulo PGRSS.
 *
 * <ul>
 *   <li>Diariamente à meia-noite: marca planos de ação vencidos e dispara notificações.</li>
 *   <li>Diariamente às 07h: dispara alertas de licenças vencidas e próximas do vencimento
 *       para todos os tenants com dados cadastrados.</li>
 * </ul>
 */
@Component
public class PgrssScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(PgrssScheduledTasks.class);

    private final PlanoAcaoResiduoRepository planoRepository;
    private final EmpresaColetorRepository empresaRepository;
    private final NotificacaoService notificacaoService;

    public PgrssScheduledTasks(PlanoAcaoResiduoRepository planoRepository,
                                EmpresaColetorRepository empresaRepository,
                                NotificacaoService notificacaoService) {
        this.planoRepository = planoRepository;
        this.empresaRepository = empresaRepository;
        this.notificacaoService = notificacaoService;
    }

    /**
     * Executa toda meia-noite. Marca planos de ação como VENCIDO quando o prazo
     * já passou e o status ainda é ABERTO ou EM_ANDAMENTO. Dispara notificação
     * por tenant para cada plano vencido encontrado.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void verificarPlanosVencidos() {
        LocalDate hoje = LocalDate.now();
        List<PlanoAcaoResiduo> candidatos = planoRepository.findAllByStatusInAndDataPrazoBefore(
                List.of(StatusPlanoAcao.ABERTO, StatusPlanoAcao.EM_ANDAMENTO), hoje);

        if (candidatos.isEmpty()) {
            return;
        }

        candidatos.forEach(p -> {
            p.setStatus(StatusPlanoAcao.VENCIDO);
            Long tenantId = p.getTenant().getId();
            notificacaoService.gerar(
                    tenantId,
                    TipoNotificacao.PGRSS_PLANO_ACAO_VENCIDO,
                    NivelNotificacao.ALERTA,
                    "Plano de ação PGRSS vencido",
                    "O plano de ação \"" + p.getDescricaoAcao() + "\" (responsável: "
                            + p.getResponsavelNome() + ") atingiu o prazo sem conclusão.",
                    p.getId(),
                    "PLANO_ACAO_RESIDUO"
            );
        });
        planoRepository.saveAll(candidatos);
        log.info("[PGRSS] {} planos de ação marcados como VENCIDO.", candidatos.size());
    }

    /**
     * Executa todos os dias às 07h. Percorre todos os tenants que possuem
     * empresas coletoras com licença vencida ou próxima do vencimento (30 dias)
     * e dispara as notificações correspondentes.
     */
    @Scheduled(cron = "0 0 7 * * *")
    @Transactional(readOnly = true)
    public void verificarLicencasEmpresas() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite30 = hoje.plusDays(30);

        // Licenças já vencidas
        List<EmpresaColetora> vencidas = empresaRepository.findAllByAtivoTrueAndDataVencimentoLicencaBefore(hoje);
        vencidas.forEach(e -> {
            Long tenantId = e.getTenant().getId();
            notificacaoService.gerar(
                    tenantId,
                    TipoNotificacao.PGRSS_LICENCA_VENCIDA,
                    NivelNotificacao.CRITICO,
                    "Licença ambiental vencida",
                    "A empresa coletora \"" + e.getRazaoSocial() + "\" possui licença ambiental vencida desde "
                            + e.getDataVencimentoLicenca() + ".",
                    e.getId(),
                    "EMPRESA_COLETORA"
            );
        });

        // Licenças próximas do vencimento (até 30 dias)
        List<EmpresaColetora> proximasVencer = empresaRepository.findAllByAtivoTrueAndDataVencimentoLicencaBetween(hoje, limite30);
        proximasVencer.forEach(e -> {
            Long tenantId = e.getTenant().getId();
            notificacaoService.gerar(
                    tenantId,
                    TipoNotificacao.PGRSS_LICENCA_PROXIMA_VENCIMENTO,
                    NivelNotificacao.ALERTA,
                    "Licença ambiental próxima do vencimento",
                    "A empresa coletora \"" + e.getRazaoSocial() + "\" tem licença ambiental vencendo em "
                            + e.getDataVencimentoLicenca() + ".",
                    e.getId(),
                    "EMPRESA_COLETORA"
            );
        });
    }
}
