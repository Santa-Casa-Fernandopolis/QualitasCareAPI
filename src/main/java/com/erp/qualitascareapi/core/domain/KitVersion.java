package com.erp.qualitascareapi.core.domain;

import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "cme_kits_versao")
public class KitVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "kit_id", nullable = false)
    private KitProcedimento kit;

    @Column(nullable = false)
    private Integer numeroVersao;

    @Column
    private LocalDate vigenciaInicio;

    @Column
    private Integer validadeDias;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aprovacao", nullable = false, length = 20)
    private StatusAprovacaoCme statusAprovacao = StatusAprovacaoCme.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aprovado_por_id")
    private User aprovadoPor;

    @Column(name = "aprovado_em")
    private LocalDateTime aprovadoEm;

    @Column(name = "revalidado_em")
    private LocalDateTime revalidadoEm;

    @Column(length = 255)
    private String observacoes;

    public KitVersion() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public KitProcedimento getKit() {
        return kit;
    }

    public void setKit(KitProcedimento kit) {
        this.kit = kit;
    }

    public Integer getNumeroVersao() {
        return numeroVersao;
    }

    public void setNumeroVersao(Integer numeroVersao) {
        this.numeroVersao = numeroVersao;
    }

    public LocalDate getVigenciaInicio() {
        return vigenciaInicio;
    }

    public void setVigenciaInicio(LocalDate vigenciaInicio) {
        this.vigenciaInicio = vigenciaInicio;
    }

    public Integer getValidadeDias() {
        return validadeDias;
    }

    public void setValidadeDias(Integer validadeDias) {
        this.validadeDias = validadeDias;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public StatusAprovacaoCme getStatusAprovacao() {
        return statusAprovacao;
    }

    public void setStatusAprovacao(StatusAprovacaoCme statusAprovacao) {
        this.statusAprovacao = statusAprovacao;
    }

    public User getAprovadoPor() {
        return aprovadoPor;
    }

    public void setAprovadoPor(User aprovadoPor) {
        this.aprovadoPor = aprovadoPor;
    }

    public LocalDateTime getAprovadoEm() {
        return aprovadoEm;
    }

    public void setAprovadoEm(LocalDateTime aprovadoEm) {
        this.aprovadoEm = aprovadoEm;
    }

    public LocalDateTime getRevalidadoEm() {
        return revalidadoEm;
    }

    public void setRevalidadoEm(LocalDateTime revalidadoEm) {
        this.revalidadoEm = revalidadoEm;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
