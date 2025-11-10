package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/** ---------------- IndicadorBiologico ---------------- */
@Audited
@Entity
@Table(name = "cme_indicadores_biologicos",
        uniqueConstraints = @UniqueConstraint(name = "uk_ib_ciclo_lote",
                columnNames = {"ciclo_id","lote_indicador"}),
        indexes = @Index(name = "ix_ib_ciclo_resultado", columnList = "ciclo_id,resultado"))
public class IndicadorBiologico {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ciclo_id", nullable = false)
    private CicloEsterilizacao ciclo;

    @NotBlank
    @Column(name = "lote_indicador", nullable = false, length = 80)
    private String loteIndicador;

    @Column(length = 80)
    private String incubadora;

    private LocalDate leituraEm;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoConformidade resultado;

    @Column(length = 800)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_indicador_biol_evidencias",
            joinColumns = @JoinColumn(name = "indicador_biologico_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public CicloEsterilizacao getCiclo() { return ciclo; }
    public void setCiclo(CicloEsterilizacao ciclo) { this.ciclo = ciclo; }
    public String getLoteIndicador() { return loteIndicador; }
    public void setLoteIndicador(String loteIndicador) { this.loteIndicador = loteIndicador; }
    public String getIncubadora() { return incubadora; }
    public void setIncubadora(String incubadora) { this.incubadora = incubadora; }
    public LocalDate getLeituraEm() { return leituraEm; }
    public void setLeituraEm(LocalDate leituraEm) { this.leituraEm = leituraEm; }
    public ResultadoConformidade getResultado() { return resultado; }
    public void setResultado(ResultadoConformidade resultado) { this.resultado = resultado; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override public boolean equals(Object o){ return o instanceof IndicadorBiologico i && Objects.equals(id, i.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
