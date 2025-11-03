package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Audited
@Entity
@Table(name = "cme_geracoes_residuo")
public class GeracaoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(nullable = false)
    private LocalDate dataRegistro;

    @Column(length = 120)
    private String tipoResiduo;

    @Column
    private Double pesoEstimadoKg;

    @Column(length = 255)
    private String destinoFinal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private LoteEtiqueta loteRelacionada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "saneante_id")
    private SaneantePeraceticoLote saneanteRelacionado;

    @Column(length = 255)
    private String observacoes;

    public GeracaoResiduo() {
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

    public LocalDate getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(LocalDate dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getTipoResiduo() {
        return tipoResiduo;
    }

    public void setTipoResiduo(String tipoResiduo) {
        this.tipoResiduo = tipoResiduo;
    }

    public Double getPesoEstimadoKg() {
        return pesoEstimadoKg;
    }

    public void setPesoEstimadoKg(Double pesoEstimadoKg) {
        this.pesoEstimadoKg = pesoEstimadoKg;
    }

    public String getDestinoFinal() {
        return destinoFinal;
    }

    public void setDestinoFinal(String destinoFinal) {
        this.destinoFinal = destinoFinal;
    }

    public LoteEtiqueta getLoteRelacionada() {
        return loteRelacionada;
    }

    public void setLoteRelacionada(LoteEtiqueta loteRelacionada) {
        this.loteRelacionada = loteRelacionada;
    }

    public SaneantePeraceticoLote getSaneanteRelacionado() {
        return saneanteRelacionado;
    }

    public void setSaneanteRelacionado(SaneantePeraceticoLote saneanteRelacionado) {
        this.saneanteRelacionado = saneanteRelacionado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
