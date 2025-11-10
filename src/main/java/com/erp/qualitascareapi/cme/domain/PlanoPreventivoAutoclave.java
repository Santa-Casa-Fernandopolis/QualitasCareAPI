package com.erp.qualitascareapi.cme.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.Objects;

/** ---------------- PlanoPreventivoAutoclave ---------------- */
@Audited
@Entity
@Table(name = "cme_plano_preventivo",
        uniqueConstraints = @UniqueConstraint(name = "uk_plano_autoclave", columnNames = "autoclave_id"))
public class PlanoPreventivoAutoclave {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @Min(0)
    @Column(nullable = false)
    private Integer periodicidadeDias = 0;

    @Min(0)
    @Column(nullable = false)
    private Integer limiteCiclos = 0;

    private LocalDate proximaExecucaoPrevista;

    @Column(length = 800)
    private String descricao;

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Autoclave getAutoclave() { return autoclave; }
    public void setAutoclave(Autoclave autoclave) { this.autoclave = autoclave; }
    public Integer getPeriodicidadeDias() { return periodicidadeDias; }
    public void setPeriodicidadeDias(Integer periodicidadeDias) { this.periodicidadeDias = periodicidadeDias; }
    public Integer getLimiteCiclos() { return limiteCiclos; }
    public void setLimiteCiclos(Integer limiteCiclos) { this.limiteCiclos = limiteCiclos; }
    public LocalDate getProximaExecucaoPrevista() { return proximaExecucaoPrevista; }
    public void setProximaExecucaoPrevista(LocalDate d) { this.proximaExecucaoPrevista = d; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override public boolean equals(Object o){ return o instanceof PlanoPreventivoAutoclave p && Objects.equals(id, p.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
