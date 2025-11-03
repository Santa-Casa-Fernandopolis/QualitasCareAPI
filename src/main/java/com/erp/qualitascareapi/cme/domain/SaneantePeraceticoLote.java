package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Audited
@Entity
@Table(name = "cme_saneantes_peracetico_lote")
public class SaneantePeraceticoLote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false, length = 120)
    private String numeroLote;

    @Column(length = 120)
    private String fabricante;

    @Column(length = 60)
    private String concentracao;

    @Column
    private LocalDate dataValidade;

    @Column
    private LocalDate dataAbertura;

    @Column
    private Double volumeInicialMl;

    @Column(length = 255)
    private String observacoes;

    public SaneantePeraceticoLote() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getConcentracao() {
        return concentracao;
    }

    public void setConcentracao(String concentracao) {
        this.concentracao = concentracao;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDate dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public Double getVolumeInicialMl() {
        return volumeInicialMl;
    }

    public void setVolumeInicialMl(Double volumeInicialMl) {
        this.volumeInicialMl = volumeInicialMl;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
