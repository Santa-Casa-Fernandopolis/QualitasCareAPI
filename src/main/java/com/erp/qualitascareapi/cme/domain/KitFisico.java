package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;
import com.erp.qualitascareapi.core.domain.KitProcedimento;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;

@Entity
@Table(name = "cme_kits_fisicos",
        uniqueConstraints = @UniqueConstraint(name = "uk_cme_kit_fis_tenant_ident", columnNames = {"tenant_id", "identificador_unico"}),
        indexes = {
                @Index(name = "ix_cme_kit_fis_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "ix_cme_kit_fis_kit", columnList = "kit_id"),
                @Index(name = "ix_cme_kit_fis_versao", columnList = "kit_versao_atual_id")
        })
public class KitFisico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kit_id", nullable = false)
    private KitProcedimento kit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_versao_atual_id")
    private KitVersion kitVersaoAtual;

    @Column(name = "identificador_unico", nullable = false, length = 80)
    private String identificadorUnico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IdentificacaoFisicaStatus status = IdentificacaoFisicaStatus.ATIVO;

    @Column(length = 120)
    private String localizacao;

    @Column(length = 800)
    private String observacoes;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public KitProcedimento getKit() { return kit; }
    public void setKit(KitProcedimento kit) { this.kit = kit; }
    public KitVersion getKitVersaoAtual() { return kitVersaoAtual; }
    public void setKitVersaoAtual(KitVersion kitVersaoAtual) { this.kitVersaoAtual = kitVersaoAtual; }
    public String getIdentificadorUnico() { return identificadorUnico; }
    public void setIdentificadorUnico(String identificadorUnico) { this.identificadorUnico = identificadorUnico; }
    public IdentificacaoFisicaStatus getStatus() { return status; }
    public void setStatus(IdentificacaoFisicaStatus status) { this.status = status; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
