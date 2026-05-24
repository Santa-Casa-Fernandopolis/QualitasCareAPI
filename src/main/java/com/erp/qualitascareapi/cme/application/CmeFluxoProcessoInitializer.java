package com.erp.qualitascareapi.cme.application;

import com.erp.qualitascareapi.cme.domain.CmeEtapaProcesso;
import com.erp.qualitascareapi.cme.domain.CmeFluxoProcesso;
import com.erp.qualitascareapi.cme.enums.CmeEtapaTipo;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import com.erp.qualitascareapi.cme.repo.CmeEtapaProcessoRepository;
import com.erp.qualitascareapi.cme.repo.CmeFluxoProcessoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class CmeFluxoProcessoInitializer implements ApplicationRunner {

    private final TenantRepository tenantRepository;
    private final CmeFluxoProcessoRepository fluxoRepository;
    private final CmeEtapaProcessoRepository etapaRepository;

    public CmeFluxoProcessoInitializer(TenantRepository tenantRepository,
                                       CmeFluxoProcessoRepository fluxoRepository,
                                       CmeEtapaProcessoRepository etapaRepository) {
        this.tenantRepository = tenantRepository;
        this.fluxoRepository = fluxoRepository;
        this.etapaRepository = etapaRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (Tenant tenant : tenantRepository.findAll()) {
            seedFluxo(tenant, TipoFluxoCME.CIRURGICO, "Fluxo cirúrgico padrão", etapasCirurgicas());
            seedFluxo(tenant, TipoFluxoCME.INHALATORIO, "Fluxo inalatório padrão", etapasInalatorias());
        }
    }

    private void seedFluxo(Tenant tenant, TipoFluxoCME tipoFluxo, String nome, List<EtapaSeed> etapas) {
        if (fluxoRepository.existsByTenantIdAndTipoFluxo(tenant.getId(), tipoFluxo)) {
            return;
        }
        CmeFluxoProcesso fluxo = new CmeFluxoProcesso();
        fluxo.setTenant(tenant);
        fluxo.setNome(nome);
        fluxo.setTipoFluxo(tipoFluxo);
        fluxo.setNumeroVersao(1);
        fluxo.setAtivo(true);
        fluxo.setDataVigenciaInicio(LocalDate.now());
        fluxo.setObservacoes("Fluxo inicial criado automaticamente pelo sistema.");
        CmeFluxoProcesso saved = fluxoRepository.save(fluxo);
        for (EtapaSeed seed : etapas) {
            CmeEtapaProcesso etapa = new CmeEtapaProcesso();
            etapa.setFluxoProcesso(saved);
            etapa.setCodigo(seed.codigo());
            etapa.setNome(seed.nome());
            etapa.setTipoEtapa(seed.tipo());
            etapa.setOrdem(seed.ordem());
            etapa.setObrigatoria(seed.obrigatoria());
            etapa.setPermitePular(seed.permitePular());
            etapa.setExigeEvidencia(seed.exigeEvidencia());
            etapa.setExigeAprovacao(seed.exigeAprovacao());
            etapa.setRotaDestino(seed.rotaDestino());
            etapaRepository.save(etapa);
        }
    }

    private List<EtapaSeed> etapasCirurgicas() {
        return List.of(
                etapa("recebimento", "Recebimento", CmeEtapaTipo.RECEBIMENTO, 10, true, false, false, false, "/cme/recebimentos"),
                etapa("limpeza-manual", "Limpeza manual", CmeEtapaTipo.LIMPEZA_MANUAL, 20, true, false, false, false, "/cme/processos"),
                etapa("ultrassonica", "Higienização ultrassônica", CmeEtapaTipo.ULTRASSONICA, 30, false, true, false, false, "/cme/ultrassonica"),
                etapa("secagem", "Secagem", CmeEtapaTipo.SECAGEM, 40, true, false, false, false, "/cme/secagens"),
                etapa("conferencia", "Conferência", CmeEtapaTipo.CONFERENCIA, 50, true, false, false, false, "/cme/secagens"),
                etapa("montagem", "Montagem e embalagem", CmeEtapaTipo.MONTAGEM, 60, true, false, false, false, "/cme/lotes"),
                etapa("esterilizacao", "Esterilização por autoclave", CmeEtapaTipo.ESTERILIZACAO, 70, true, false, true, true, "/cme/ciclos"),
                etapa("liberacao", "Liberação", CmeEtapaTipo.LIBERACAO, 80, true, false, false, true, "/cme/aprovacoes-cme"),
                etapa("estoque", "Estoque estéril", CmeEtapaTipo.ESTOQUE, 90, true, false, false, false, "/cme/lotes")
        );
    }

    private List<EtapaSeed> etapasInalatorias() {
        return List.of(
                etapa("recebimento", "Recebimento", CmeEtapaTipo.RECEBIMENTO, 10, true, false, false, false, "/cme/recebimentos"),
                etapa("limpeza-manual", "Limpeza manual", CmeEtapaTipo.LIMPEZA_MANUAL, 20, true, false, false, false, "/cme/processos"),
                etapa("banho-quimico", "Desinfecção química", CmeEtapaTipo.BANHO_QUIMICO, 30, true, false, true, false, "/cme/usos-saneante"),
                etapa("secagem", "Secagem", CmeEtapaTipo.SECAGEM, 40, true, false, false, false, "/cme/secagens"),
                etapa("conferencia", "Conferência", CmeEtapaTipo.CONFERENCIA, 50, true, false, false, false, "/cme/secagens"),
                etapa("montagem", "Montagem e embalagem", CmeEtapaTipo.MONTAGEM, 60, true, false, false, false, "/cme/lotes"),
                etapa("liberacao", "Liberação", CmeEtapaTipo.LIBERACAO, 70, true, false, false, true, "/cme/aprovacoes-cme"),
                etapa("estoque", "Estoque desinfetado", CmeEtapaTipo.ESTOQUE, 80, true, false, false, false, "/cme/lotes")
        );
    }

    private EtapaSeed etapa(String codigo, String nome, CmeEtapaTipo tipo, int ordem, boolean obrigatoria,
                            boolean permitePular, boolean exigeEvidencia, boolean exigeAprovacao, String rotaDestino) {
        return new EtapaSeed(codigo, nome, tipo, ordem, obrigatoria, permitePular, exigeEvidencia, exigeAprovacao, rotaDestino);
    }

    private record EtapaSeed(String codigo, String nome, CmeEtapaTipo tipo, int ordem, boolean obrigatoria,
                             boolean permitePular, boolean exigeEvidencia, boolean exigeAprovacao, String rotaDestino) {}
}
