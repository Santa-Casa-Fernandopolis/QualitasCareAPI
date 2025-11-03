package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "cme_higienizacoes_autoclave_profunda")
public class HigienizacaoAutoclaveProfunda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @Column(nullable = false)
    private LocalDate dataRealizacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_hig_autoclave_evidencias",
            joinColumns = @JoinColumn(name = "higienizacao_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public HigienizacaoAutoclaveProfunda() {
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

    public LocalDate getDataRealizacao() {
        return dataRealizacao;
    }

    public void setDataRealizacao(LocalDate dataRealizacao) {
        this.dataRealizacao = dataRealizacao;
    }

    public User getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(User responsavel) {
        this.responsavel = responsavel;
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
