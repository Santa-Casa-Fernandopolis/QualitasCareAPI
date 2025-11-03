package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "cme_testes_bowie_dick")
public class TesteBowieDick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @Column(nullable = false)
    private LocalDate dataExecucao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoConformidade resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executado_por_id")
    private User executadoPor;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_bowiedick_evidencias",
            joinColumns = @JoinColumn(name = "teste_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public TesteBowieDick() {
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

    public LocalDate getDataExecucao() {
        return dataExecucao;
    }

    public void setDataExecucao(LocalDate dataExecucao) {
        this.dataExecucao = dataExecucao;
    }

    public ResultadoConformidade getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoConformidade resultado) {
        this.resultado = resultado;
    }

    public User getExecutadoPor() {
        return executadoPor;
    }

    public void setExecutadoPor(User executadoPor) {
        this.executadoPor = executadoPor;
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
