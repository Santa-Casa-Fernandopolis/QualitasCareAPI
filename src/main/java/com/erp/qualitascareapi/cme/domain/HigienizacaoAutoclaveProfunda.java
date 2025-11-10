package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/** ---------------- HigienizacaoAutoclaveProfunda ---------------- */
@Audited
@Entity
@Table(name = "cme_higienizacoes_autoclave_profunda",
        indexes = @Index(name = "ix_hig_prof_autoclave_data", columnList = "autoclave_id,data_realizacao"))
public class HigienizacaoAutoclaveProfunda {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @NotNull
    @Column(name = "data_realizacao", nullable = false)
    private LocalDate dataRealizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_hig_prof_evidencias",
            joinColumns = @JoinColumn(name = "hig_profunda_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Autoclave getAutoclave() { return autoclave; }
    public void setAutoclave(Autoclave autoclave) { this.autoclave = autoclave; }
    public LocalDate getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(LocalDate dataRealizacao) { this.dataRealizacao = dataRealizacao; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override public boolean equals(Object o){ return o instanceof HigienizacaoAutoclaveProfunda h && Objects.equals(id, h.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
