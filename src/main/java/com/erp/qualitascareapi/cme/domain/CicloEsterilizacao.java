package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.CicloStatus;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Audited
@Entity
@Table(name = "cme_ciclos_esterilizacao")
public class CicloEsterilizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private LoteEtiqueta loteEtiqueta;

    @Column(nullable = false)
    private LocalDateTime inicio;

    @Column
    private LocalDateTime fim;

    @Column
    private Integer duracaoMinutos;

    @Column
    private Double temperaturaMaxima;

    @Column
    private Double pressaoMaxima;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CicloStatus status = CicloStatus.AGENDADO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liberado_por_id")
    private User liberadoPor;

    @Column(length = 255)
    private String observacoes;

    public CicloEsterilizacao() {
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

    public Autoclave getAutoclave() {
        return autoclave;
    }

    public void setAutoclave(Autoclave autoclave) {
        this.autoclave = autoclave;
    }

    public LoteEtiqueta getLoteEtiqueta() {
        return loteEtiqueta;
    }

    public void setLoteEtiqueta(LoteEtiqueta loteEtiqueta) {
        this.loteEtiqueta = loteEtiqueta;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalDateTime inicio) {
        this.inicio = inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public void setFim(LocalDateTime fim) {
        this.fim = fim;
    }

    public Integer getDuracaoMinutos() {
        return duracaoMinutos;
    }

    public void setDuracaoMinutos(Integer duracaoMinutos) {
        this.duracaoMinutos = duracaoMinutos;
    }

    public Double getTemperaturaMaxima() {
        return temperaturaMaxima;
    }

    public void setTemperaturaMaxima(Double temperaturaMaxima) {
        this.temperaturaMaxima = temperaturaMaxima;
    }

    public Double getPressaoMaxima() {
        return pressaoMaxima;
    }

    public void setPressaoMaxima(Double pressaoMaxima) {
        this.pressaoMaxima = pressaoMaxima;
    }

    public CicloStatus getStatus() {
        return status;
    }

    public void setStatus(CicloStatus status) {
        this.status = status;
    }

    public User getLiberadoPor() {
        return liberadoPor;
    }

    public void setLiberadoPor(User liberadoPor) {
        this.liberadoPor = liberadoPor;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}
