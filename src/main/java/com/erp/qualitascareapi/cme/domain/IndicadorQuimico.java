package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/** ---------------- IndicadorQuimico ---------------- */
@Audited
@Entity
@Table(name = "cme_indicadores_quimicos",
        indexes = @Index(name = "ix_iq_ciclo_resultado", columnList = "ciclo_id,resultado"))
public class IndicadorQuimico {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ciclo_id", nullable = false)
    private CicloEsterilizacao ciclo;

    @Column(length = 80)
    private String tipo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoConformidade resultado;

    @Column(length = 800)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_indicador_quim_evidencias",
            joinColumns = @JoinColumn(name = "indicador_quimico_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public CicloEsterilizacao getCiclo() { return ciclo; }
    public void setCiclo(CicloEsterilizacao ciclo) { this.ciclo = ciclo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public ResultadoConformidade getResultado() { return resultado; }
    public void setResultado(ResultadoConformidade resultado) { this.resultado = resultado; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override public boolean equals(Object o){ return o instanceof IndicadorQuimico i && Objects.equals(id, i.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
