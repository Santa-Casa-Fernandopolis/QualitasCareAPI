package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.ExameCulturaDto;
import com.erp.qualitascareapi.cme.api.dto.ExameCulturaRequest;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.core.domain.ExameCultura;
import com.erp.qualitascareapi.core.repo.ExameCulturaRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class QualidadeService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final ExameCulturaRepository exameCulturaRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public QualidadeService(TenantRepository tenantRepository,
                            UserRepository userRepository,
                            ExameCulturaRepository exameCulturaRepository,
                            EvidenciaArquivoRepository evidenciaArquivoRepository,
                            TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.exameCulturaRepository = exameCulturaRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public ExameCulturaDto registrarExame(ExameCulturaRequest request) {
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        ExameCultura exame = new ExameCultura();
        exame.setTenant(tenant);
        exame.setOrigemAmostra(request.origemAmostra());
        exame.setDataColeta(request.dataColeta());
        exame.setResponsavelColeta(request.responsavelColeta());
        if (request.resultado() != null) {
            exame.setResultado(request.resultado());
        }
        if (request.registradoPorId() != null) {
            User user = userRepository.findById(request.registradoPorId())
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
            exame.setRegistradoPor(user);
        }
        exame.setObservacoes(request.observacoes());
        exame.setEvidencias(loadEvidencias(request.evidenciasIds()));
        ExameCultura saved = exameCulturaRepository.save(exame);
        return new ExameCulturaDto(saved.getId(), tenant.getId(), saved.getOrigemAmostra(), saved.getDataColeta(),
                saved.getResponsavelColeta(), saved.getResultado(),
                saved.getRegistradoPor() != null ? saved.getRegistradoPor().getId() : null,
                saved.getObservacoes(), toIdSet(saved.getEvidencias()));
    }

    public Page<ExameCulturaDto> listExames(Pageable pageable) {
        return exameCulturaRepository.findAllByTenantId(tenantScopeGuard.currentTenantId(), pageable)
                .map(exame -> new ExameCulturaDto(exame.getId(), exame.getTenant().getId(),
                        exame.getOrigemAmostra(), exame.getDataColeta(), exame.getResponsavelColeta(),
                        exame.getResultado(),
                        exame.getRegistradoPor() != null ? exame.getRegistradoPor().getId() : null,
                        exame.getObservacoes(), toIdSet(exame.getEvidencias())));
    }

    private Set<EvidenciaArquivo> loadEvidencias(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptySet();
        }
        return new HashSet<>(evidenciaArquivoRepository.findAllById(ids));
    }

    private Set<Long> toIdSet(Set<EvidenciaArquivo> evidencias) {
        if (evidencias == null || evidencias.isEmpty()) {
            return Collections.emptySet();
        }
        return evidencias.stream().map(EvidenciaArquivo::getId).collect(Collectors.toSet());
    }
}
