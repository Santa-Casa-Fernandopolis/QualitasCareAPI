package com.erp.qualitascareapi.config;

import com.erp.qualitascareapi.pgrss.application.PgrssScheduledTasks;
import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.support.CronTrigger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Configura os jobs agendados do módulo PGRSS com cron dinâmico lido de
 * {@code sys_configuracoes}. O cron e o flag ativo são relidos a cada tick,
 * permitindo ajuste sem restart.
 *
 * <p>Parâmetros lidos (módulo {@code PGRSS}):</p>
 * <ul>
 *   <li>{@code PGRSS_PLANOS_VENCIDOS_ATIVO} — boolean, habilita o job de planos</li>
 *   <li>{@code PGRSS_PLANOS_VENCIDOS_CRON}  — cron expression, padrão {@value #CRON_PLANOS_DEFAULT}</li>
 *   <li>{@code PGRSS_LICENCAS_ATIVO}        — boolean, habilita o job de licenças</li>
 *   <li>{@code PGRSS_LICENCAS_CRON}         — cron expression, padrão {@value #CRON_LICENCAS_DEFAULT}</li>
 * </ul>
 */
@Configuration
@EnableScheduling
public class PgrssSchedulingConfig implements SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(PgrssSchedulingConfig.class);

    static final String CRON_PLANOS_DEFAULT  = "0 0 0 * * *";
    static final String CRON_LICENCAS_DEFAULT = "0 0 7 * * *";
    private static final int SLEEP_INATIVO_HORAS = 1;

    private final PgrssScheduledTasks pgrssScheduledTasks;
    private final ConfiguracaoService configuracaoService;

    public PgrssSchedulingConfig(PgrssScheduledTasks pgrssScheduledTasks,
                                  ConfiguracaoService configuracaoService) {
        this.pgrssScheduledTasks = pgrssScheduledTasks;
        this.configuracaoService = configuracaoService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addTriggerTask(
                this::executarVerificarPlanosVencidos,
                triggerContext -> proximaExecucao(
                        "PGRSS_PLANOS_VENCIDOS_ATIVO",
                        "PGRSS_PLANOS_VENCIDOS_CRON",
                        CRON_PLANOS_DEFAULT,
                        triggerContext)
        );

        registrar.addTriggerTask(
                this::executarVerificarLicencas,
                triggerContext -> proximaExecucao(
                        "PGRSS_LICENCAS_ATIVO",
                        "PGRSS_LICENCAS_CRON",
                        CRON_LICENCAS_DEFAULT,
                        triggerContext)
        );
    }

    private void executarVerificarPlanosVencidos() {
        try {
            pgrssScheduledTasks.verificarPlanosVencidos();
        } catch (Exception e) {
            log.error("[PGRSS-PLANOS] Erro inesperado no job agendado: {}", e.getMessage(), e);
        }
    }

    private void executarVerificarLicencas() {
        try {
            pgrssScheduledTasks.verificarLicencasEmpresas();
        } catch (Exception e) {
            log.error("[PGRSS-LICENCAS] Erro inesperado no job agendado: {}", e.getMessage(), e);
        }
    }

    private Instant proximaExecucao(String chaveAtivo, String chaveCron, String cronDefault,
                                     org.springframework.scheduling.TriggerContext triggerContext) {
        boolean ativo = configuracaoService.getValorBoolean(ModuloConfiguracao.PGRSS, chaveAtivo, true);
        if (!ativo) {
            return Instant.now().plus(SLEEP_INATIVO_HORAS, ChronoUnit.HOURS);
        }
        String cron = configuracaoService.getValor(ModuloConfiguracao.PGRSS, chaveCron);
        if (cron == null || cron.isBlank()) {
            cron = cronDefault;
        }
        return new CronTrigger(cron).nextExecution(triggerContext);
    }
}
