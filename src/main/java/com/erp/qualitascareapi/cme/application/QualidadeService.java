package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.domain.LoteEtiqueta;
import com.erp.qualitascareapi.cme.domain.NaoConformidadeCME;
import com.erp.qualitascareapi.cme.domain.SaneantePeraceticoLote;
import com.erp.qualitascareapi.cme.repo.LoteEtiquetaRepository;
import com.erp.qualitascareapi.cme.repo.NaoConformidadeCMERepository;
import com.erp.qualitascareapi.cme.repo.SaneantePeraceticoLoteRepository;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.core.domain.ExameCultura;
import com.erp.qualitascareapi.core.repo.ExameCulturaRepository;
import com.erp.qualitascareapi.environmental.domain.GeracaoResiduo;
import com.erp.qualitascareapi.environmental.repo.GeracaoResiduoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.quality.domain.TipoNaoConformidade;
import com.erp.qualitascareapi.quality.repo.TipoNaoConformidadeRepository;
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
    private final NaoConformidadeCMERepository naoConformidadeRepository;
    private final TipoNaoConformidadeRepository tipoNaoConformidadeRepository;
    private final GeracaoResiduoRepository geracaoResiduoRepository;
    private final LoteEtiquetaRepository loteEtiquetaRepository;
    private final SaneantePeraceticoLoteRepository saneanteRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;

    public QualidadeService(TenantRepository tenantRepository,
                            UserRepository userRepository,
                            ExameCulturaRepository exameCulturaRepository,
                            NaoConformidadeCMERepository naoConformidadeRepository,
                            TipoNaoConformidadeRepository tipoNaoConformidadeRepository,
                            GeracaoResiduoRepository geracaoResiduoRepository,
                            LoteEtiquetaRepository loteEtiquetaRepository,
                            SaneantePeraceticoLoteRepository saneanteRepository,
                            EvidenciaArquivoRepository evidenciaArquivoRepository) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.exameCulturaRepository = exameCulturaRepository;
        this.naoConformidadeRepository = naoConformidadeRepository;
        this.tipoNaoConformidadeRepository = tipoNaoConformidadeRepository;
        this.geracaoResiduoRepository = geracaoResiduoRepository;
        this.loteEtiquetaRepository = loteEtiquetaRepository;
        this.saneanteRepository = saneanteRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
    }

    public ExameCulturaDto registrarExame(ExameCulturaRequest request) {
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
        return exameCulturaRepository.findAll(pageable)
                .map(exame -> new ExameCulturaDto(exame.getId(), exame.getTenant().getId(),
                        exame.getOrigemAmostra(), exame.getDataColeta(), exame.getResponsavelColeta(),
                        exame.getResultado(),
                        exame.getRegistradoPor() != null ? exame.getRegistradoPor().getId() : null,
                        exame.getObservacoes(), toIdSet(exame.getEvidencias())));
    }

    public NaoConformidadeDto registrarNaoConformidade(NaoConformidadeRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        TipoNaoConformidade tipo = tipoNaoConformidadeRepository.findById(request.tipoId())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de não conformidade não encontrado"));
        NaoConformidadeCME naoConformidade = new NaoConformidadeCME();
        naoConformidade.setTenant(tenant);
        naoConformidade.setTipo(tipo);
        naoConformidade.setTitulo(request.titulo());
        naoConformidade.setDescricao(request.descricao());
        naoConformidade.setSeveridade(request.severidade());
        if (request.status() != null) {
            naoConformidade.setStatus(request.status());
        }
        naoConformidade.setDataAbertura(request.dataAbertura());
        naoConformidade.setDataEncerramento(request.dataEncerramento());
        if (request.responsavelId() != null) {
            User user = userRepository.findById(request.responsavelId())
                    .orElseThrow(() -> new EntityNotFoundException("Responsável não encontrado"));
            naoConformidade.setResponsavel(user);
        }
        naoConformidade.setPlanoAcaoResumo(request.planoAcaoResumo());
        naoConformidade.setEvidencias(loadEvidencias(request.evidenciasIds()));
        NaoConformidadeCME saved = naoConformidadeRepository.save(naoConformidade);
        return new NaoConformidadeDto(saved.getId(), tenant.getId(), saved.getTitulo(), saved.getDescricao(),
                saved.getSeveridade(), saved.getStatus(), saved.getDataAbertura(), saved.getDataEncerramento(),
                saved.getResponsavel() != null ? saved.getResponsavel().getId() : null,
                saved.getPlanoAcaoResumo(), toIdSet(saved.getEvidencias()), saved.getTipo().getId());
    }

    public Page<NaoConformidadeDto> listNaoConformidades(Pageable pageable) {
        return naoConformidadeRepository.findAll(pageable)
                .map(nc -> new NaoConformidadeDto(nc.getId(), nc.getTenant().getId(), nc.getTitulo(), nc.getDescricao(),
                        nc.getSeveridade(), nc.getStatus(), nc.getDataAbertura(), nc.getDataEncerramento(),
                        nc.getResponsavel() != null ? nc.getResponsavel().getId() : null,
                        nc.getPlanoAcaoResumo(), toIdSet(nc.getEvidencias()),
                        nc.getTipo() != null ? nc.getTipo().getId() : null));
    }

    public GeracaoResiduoDto registrarGeracaoResiduo(GeracaoResiduoRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new EntityNotFoundException("Tenant não encontrado"));
        GeracaoResiduo residuo = new GeracaoResiduo();
        residuo.setTenant(tenant);
        residuo.setDataRegistro(request.dataRegistro());
        residuo.setClasseResiduo(request.classeResiduo());
        residuo.setPesoEstimadoKg(request.pesoEstimadoKg());
        residuo.setDestinoFinal(request.destinoFinal());
        if (request.loteId() != null) {
            LoteEtiqueta lote = loteEtiquetaRepository.findById(request.loteId())
                    .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
            residuo.setLoteRelacionada(lote);
        }
        if (request.saneanteId() != null) {
            SaneantePeraceticoLote saneante = saneanteRepository.findById(request.saneanteId())
                    .orElseThrow(() -> new EntityNotFoundException("Saneante não encontrado"));
            residuo.setSaneanteRelacionado(saneante);
        }
        residuo.setObservacoes(request.observacoes());
        GeracaoResiduo saved = geracaoResiduoRepository.save(residuo);
        return new GeracaoResiduoDto(saved.getId(), tenant.getId(), saved.getDataRegistro(), saved.getClasseResiduo(),
                saved.getPesoEstimadoKg(), saved.getDestinoFinal(),
                saved.getLoteRelacionada() != null ? saved.getLoteRelacionada().getId() : null,
                saved.getSaneanteRelacionado() != null ? saved.getSaneanteRelacionado().getId() : null,
                saved.getObservacoes());
    }

    public Page<GeracaoResiduoDto> listGeracoesResiduo(Pageable pageable) {
        return geracaoResiduoRepository.findAll(pageable)
                .map(g -> new GeracaoResiduoDto(g.getId(), g.getTenant().getId(), g.getDataRegistro(), g.getClasseResiduo(),
                        g.getPesoEstimadoKg(), g.getDestinoFinal(),
                        g.getLoteRelacionada() != null ? g.getLoteRelacionada().getId() : null,
                        g.getSaneanteRelacionado() != null ? g.getSaneanteRelacionado().getId() : null,
                        g.getObservacoes()));
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
