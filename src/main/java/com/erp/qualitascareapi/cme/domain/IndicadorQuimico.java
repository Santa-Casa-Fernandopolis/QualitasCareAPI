package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "cme_indicadores_quimicos")
public class IndicadorQuimico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ciclo_id", nullable = false)
    private CicloEsterilizacao ciclo;

    @Column(length = 120)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoConformidade resultado;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_indicador_quimico_evidencias",
            joinColumns = @JoinColumn(name = "indicador_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public IndicadorQuimico() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CicloEsterilizacao getCiclo() {
        return ciclo;
    }

    public void setCiclo(CicloEsterilizacao ciclo) {
        this.ciclo = ciclo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public ResultadoConformidade getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoConformidade resultado) {
        this.resultado = resultado;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Set<EvidenciaArquivo> getEvidencias() {
        return evidencias;
    }

    public void setEvidencias(Set<EvidenciaArquivo> evidencias) {
        this.evidencias = evidencias;
    }
}
