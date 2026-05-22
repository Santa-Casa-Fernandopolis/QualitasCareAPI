package com.erp.qualitascareapi.same.legacy;

import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SoulMvPatientConnector extends AbstractJdbcLegacyPatientConnector {

    private final ConfiguracaoService configuracaoService;

    public SoulMvPatientConnector(Environment environment,
                                  TextEncryptor encryptor,
                                  ConfiguracaoService configuracaoService) {
        super(environment, encryptor);
        this.configuracaoService = configuracaoService;
    }

    @Override
    public SameSourceSystem sourceSystem() {
        return SameSourceSystem.SOUL_MV;
    }

    @Override
    protected List<LegacyConnectionConfig> activeConnections(Long tenantId) {
        String url = configuracaoService.getValorComFallback(ModuloConfiguracao.MV, "MV_DB_URL", tenantId);
        String username = configuracaoService.getValorComFallback(ModuloConfiguracao.MV, "MV_DB_USERNAME", tenantId);
        String password = configuracaoService.getValorComFallback(ModuloConfiguracao.MV, "MV_DB_PASSWORD", tenantId);
        if (url == null || url.isBlank()) {
            return List.of();
        }
        return List.of(new LegacyConnectionConfig(url, username, password));
    }
}
