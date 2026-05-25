package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;
import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import com.erp.qualitascareapi.core.domain.KitProcedimento;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kit_id")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aprovacao", nullable = false, length = 20)
    private StatusAprovacaoCme statusAprovacao = StatusAprovacaoCme.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovado_por_id")
    private User aprovadoPor;

    @Column(name = "aprovado_em")
    private LocalDateTime aprovadoEm;

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
    public StatusAprovacaoCme getStatusAprovacao() { return statusAprovacao; }
    public void setStatusAprovacao(StatusAprovacaoCme statusAprovacao) { this.statusAprovacao = statusAprovacao; }
    public User getAprovadoPor() { return aprovadoPor; }
    public void setAprovadoPor(User aprovadoPor) { this.aprovadoPor = aprovadoPor; }
    public LocalDateTime getAprovadoEm() { return aprovadoEm; }
    public void setAprovadoEm(LocalDateTime aprovadoEm) { this.aprovadoEm = aprovadoEm; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}
