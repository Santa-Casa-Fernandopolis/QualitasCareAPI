package com.erp.qualitascareapi.same.legacy;

import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.same.repo.SameLegacyIntegrationSourceRepository;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class WirelinePatientConnector extends AbstractJdbcLegacyPatientConnector {

    private final SameLegacyIntegrationSourceRepository sourceRepository;

    public WirelinePatientConnector(Environment environment,
                                    TextEncryptor encryptor,
                                    SameLegacyIntegrationSourceRepository sourceRepository) {
        super(environment, encryptor);
        this.sourceRepository = sourceRepository;
    }

    @Override
    public SameSourceSystem sourceSystem() {
        return SameSourceSystem.WIRELINE;
    }

    @Override
    protected List<LegacyConnectionConfig> activeConnections(Long tenantId) {
        return sourceRepository.findAllByTenantIdAndSourceSystemAndActiveTrue(tenantId, SameSourceSystem.WIRELINE)
                .stream()
                .map(source -> new LegacyConnectionConfig(
                        source.getJdbcUrl(),
                        source.getUsername(),
                        source.getEncryptedPassword()))
                .toList();
    }
}
