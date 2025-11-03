package com.erp.qualitascareapi.cme.domain;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;

@Audited
@Entity
@Table(name = "cme_planos_preventivo_autoclave")
public class PlanoPreventivoAutoclave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @Column
    private Integer periodicidadeDias;

    @Column
    private Integer limiteCiclos;

    @Column
    private LocalDate proximaExecucaoPrevista;

    @Column(length = 255)
    private String descricao;

    public PlanoPreventivoAutoclave() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Autoclave getAutoclave() {
        return autoclave;
    }

    public void setAutoclave(Autoclave autoclave) {
        this.autoclave = autoclave;
    }

    public Integer getPeriodicidadeDias() {
        return periodicidadeDias;
    }

    public void setPeriodicidadeDias(Integer periodicidadeDias) {
        this.periodicidadeDias = periodicidadeDias;
    }

    public Integer getLimiteCiclos() {
        return limiteCiclos;
    }

    public void setLimiteCiclos(Integer limiteCiclos) {
        this.limiteCiclos = limiteCiclos;
    }

    public LocalDate getProximaExecucaoPrevista() {
        return proximaExecucaoPrevista;
    }

    public void setProximaExecucaoPrevista(LocalDate proximaExecucaoPrevista) {
        this.proximaExecucaoPrevista = proximaExecucaoPrevista;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
