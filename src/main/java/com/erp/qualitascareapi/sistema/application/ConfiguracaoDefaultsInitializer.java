package com.erp.qualitascareapi.sistema.application;

import com.erp.qualitascareapi.sistema.domain.ConfiguracaoSistema;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import com.erp.qualitascareapi.sistema.enums.TipoValorConfiguracao;
import com.erp.qualitascareapi.sistema.repo.ConfiguracaoSistemaRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConfiguracaoDefaultsInitializer {

    private final ConfiguracaoSistemaRepository repository;

    public ConfiguracaoDefaultsInitializer(ConfiguracaoSistemaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @EventListener(ApplicationReadyEvent.class)
    public void seedDefaults() {
        List<ConfiguracaoSistema> missing = new ArrayList<>();
        for (DefaultConfig config : defaults()) {
            if (!repository.existsByModuloAndChaveAndTenantIdIsNull(config.modulo(), config.chave())) {
                missing.add(toEntity(config));
            }
        }
        if (!missing.isEmpty()) {
            repository.saveAll(missing);
        }
    }

    private ConfiguracaoSistema toEntity(DefaultConfig config) {
        ConfiguracaoSistema entity = new ConfiguracaoSistema();
        entity.setTenantId(null);
        entity.setModulo(config.modulo());
        entity.setChave(config.chave());
        entity.setValor(config.valor());
        entity.setTipoValor(config.tipoValor());
        entity.setDescricao(config.descricao());
        entity.setEditavel(config.editavel());
        return entity;
    }

    private List<DefaultConfig> defaults() {
        return List.of(
                new DefaultConfig(ModuloConfiguracao.SISTEMA, "SISTEMA_NOME", "QualitasCare",
                        TipoValorConfiguracao.STRING, "Nome do sistema exibido na interface", true),
                new DefaultConfig(ModuloConfiguracao.SISTEMA, "SISTEMA_VERSAO", "1.0.0",
                        TipoValorConfiguracao.STRING, "Versão atual do sistema", false),
                new DefaultConfig(ModuloConfiguracao.SISTEMA, "NOTIFICACAO_EMAIL_ATIVO", "false",
                        TipoValorConfiguracao.BOOLEAN, "Habilita envio de e-mail junto com notificações in-app", true),
                new DefaultConfig(ModuloConfiguracao.SISTEMA, "NOTIFICACAO_EMAIL_FROM", "noreply@qualitascare.com",
                        TipoValorConfiguracao.STRING, "Endereço de e-mail remetente das notificações", true),
                new DefaultConfig(ModuloConfiguracao.SISTEMA, "NOTIFICACAO_EMAIL_DESTINATARIOS", "",
                        TipoValorConfiguracao.STRING, "Lista de e-mails destinatários separada por vírgula", true),

                new DefaultConfig(ModuloConfiguracao.MV, "MV_INTEGRACAO_ATIVA", "false",
                        TipoValorConfiguracao.BOOLEAN, "Habilita a sincronização automática de cirurgias do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_INTEGRACAO_TIPO", "API",
                        TipoValorConfiguracao.STRING, "Tipo de integração com Soul MV: API ou BANCO_DADOS", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_TENANT_ID", "",
                        TipoValorConfiguracao.INTEGER, "ID do tenant que receberá as cirurgias importadas do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_API_URL", "",
                        TipoValorConfiguracao.STRING, "URL base da API REST do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_API_CLIENT_ID", "",
                        TipoValorConfiguracao.STRING, "Client ID OAuth2 para autenticação na API do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_API_CLIENT_SECRET", "",
                        TipoValorConfiguracao.SECRET, "Client Secret OAuth2 para autenticação na API do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_API_TOKEN_URL", "",
                        TipoValorConfiguracao.STRING, "URL do endpoint OAuth2 de token do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_DB_URL", "",
                        TipoValorConfiguracao.STRING, "JDBC URL do banco de dados do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_DB_USERNAME", "",
                        TipoValorConfiguracao.STRING, "Usuário de leitura do banco de dados do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_DB_PASSWORD", "",
                        TipoValorConfiguracao.SECRET, "Senha do banco de dados do Soul MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_DB_QUERY_CIRURGIAS", "",
                        TipoValorConfiguracao.STRING, "SQL customizado para buscar cirurgias no banco MV; se vazio, usa a query padrão", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_SYNC_INTERVALO_MIN", "15",
                        TipoValorConfiguracao.INTEGER, "Intervalo em minutos entre sincronizações de cirurgias agendadas do MV", true),
                new DefaultConfig(ModuloConfiguracao.MV, "MV_SYNC_DIAS_ANTECEDENCIA", "7",
                        TipoValorConfiguracao.INTEGER, "Quantos dias à frente buscar cirurgias agendadas do MV", true),

                new DefaultConfig(ModuloConfiguracao.SAME, "SAME_MAX_FILE_SIZE_BYTES", "52428800",
                        TipoValorConfiguracao.INTEGER, "Tamanho máximo em bytes permitido para upload de PDF de prontuário", true),

                new DefaultConfig(ModuloConfiguracao.PGRSS, "PGRSS_PLANOS_VENCIDOS_ATIVO", "true",
                        TipoValorConfiguracao.BOOLEAN, "Habilita o job diário que marca planos de ação PGRSS como vencido", true),
                new DefaultConfig(ModuloConfiguracao.PGRSS, "PGRSS_PLANOS_VENCIDOS_CRON", "0 0 0 * * *",
                        TipoValorConfiguracao.STRING, "Cron expression do job de verificação de planos vencidos", true),
                new DefaultConfig(ModuloConfiguracao.PGRSS, "PGRSS_LICENCAS_ATIVO", "true",
                        TipoValorConfiguracao.BOOLEAN, "Habilita o job diário que verifica licenças de empresas coletoras", true),
                new DefaultConfig(ModuloConfiguracao.PGRSS, "PGRSS_LICENCAS_CRON", "0 0 7 * * *",
                        TipoValorConfiguracao.STRING, "Cron expression do job de verificação de licenças", true)
        );
    }

    private record DefaultConfig(
            ModuloConfiguracao modulo,
            String chave,
            String valor,
            TipoValorConfiguracao tipoValor,
            String descricao,
            boolean editavel
    ) {
    }
}
