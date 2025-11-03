package com.erp.qualitascareapi.core.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

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

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
