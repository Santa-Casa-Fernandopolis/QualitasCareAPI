package com.erp.qualitascareapi.config;

import com.erp.qualitascareapi.integracao.mv.application.MvIntegracaoService;
import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Configura o job de sincronização MV com intervalo dinâmico lido de
 * {@code sys_configuracoes} (parâmetro {@code MV_SYNC_INTERVALO_MIN}).
 *
 * <h3>Comportamento</h3>
 * <ul>
 *   <li>Se {@code MV_INTEGRACAO_ATIVA = false}: próxima execução agendada para 1 hora,
 *       evitando busy-loop sem consumir recursos.</li>
 *   <li>Se ativa: usa o intervalo configurado (padrão 15 min).
 *       O intervalo é relido a cada tick — trocar o parâmetro no banco já altera
 *       a cadência sem restart.</li>
 * </ul>
 */
@Configuration
@EnableScheduling
public class MvSyncSchedulingConfig implements SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(MvSyncSchedulingConfig.class);
    private static final int INTERVALO_PADRAO_MIN   = 15;
    private static final int SLEEP_INATIVO_MIN      = 60;

    private final MvIntegracaoService mvIntegracaoService;
    private final ConfiguracaoService configuracaoService;

    public MvSyncSchedulingConfig(MvIntegracaoService mvIntegracaoService,
                                  ConfiguracaoService configuracaoService) {
        this.mvIntegracaoService = mvIntegracaoService;
        this.configuracaoService = configuracaoService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.addTriggerTask(
                this::executarSync,
                triggerContext -> {
                    boolean ativa = configuracaoService.getValorBoolean(
                            ModuloConfiguracao.MV, "MV_INTEGRACAO_ATIVA", false);

                    int intervaloMin = ativa
                            ? configuracaoService.getValorInt(ModuloConfiguracao.MV, "MV_SYNC_INTERVALO_MIN")
                                    .orElse(INTERVALO_PADRAO_MIN)
                            : SLEEP_INATIVO_MIN;

                    Instant referencia = triggerContext.lastCompletion() != null
                            ? triggerContext.lastCompletion()
                            : Instant.now();

                    Instant proxima = referencia.plus(intervaloMin, ChronoUnit.MINUTES);
                    log.debug("[MV-SYNC] Próxima execução em {} min (ativa={})", intervaloMin, ativa);
                    return proxima;
                }
        );
    }

    private void executarSync() {
        try {
            mvIntegracaoService.sincronizarCirurgias();
        } catch (Exception e) {
            // Nunca deixar o trigger quebrar — log e continua agendamento
            log.error("[MV-SYNC] Erro inesperado no job agendado: {}", e.getMessage(), e);
        }
    }
}
