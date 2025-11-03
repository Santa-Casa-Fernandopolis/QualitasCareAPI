package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ManutencaoStatus;
import com.erp.qualitascareapi.cme.enums.ManutencaoTipo;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Audited
@Entity
@Table(name = "cme_manutencoes_autoclave")
public class ManutencaoAutoclave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ManutencaoTipo tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ManutencaoStatus status = ManutencaoStatus.ABERTA;

    @Column
    private LocalDate dataAgendamento;

    @Column
    private LocalDate dataExecucao;

    @Column(length = 150)
    private String responsavelTecnico;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_manutencao_evidencias",
            joinColumns = @JoinColumn(name = "manutencao_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public ManutencaoAutoclave() {
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

    public ManutencaoTipo getTipo() {
        return tipo;
    }

    public void setTipo(ManutencaoTipo tipo) {
        this.tipo = tipo;
    }

    public ManutencaoStatus getStatus() {
        return status;
    }

    public void setStatus(ManutencaoStatus status) {
        this.status = status;
    }

    public LocalDate getDataAgendamento() {
        return dataAgendamento;
    }

    public void setDataAgendamento(LocalDate dataAgendamento) {
        this.dataAgendamento = dataAgendamento;
    }

    public LocalDate getDataExecucao() {
        return dataExecucao;
    }

    public void setDataExecucao(LocalDate dataExecucao) {
        this.dataExecucao = dataExecucao;
    }

    public String getResponsavelTecnico() {
        return responsavelTecnico;
    }

    public void setResponsavelTecnico(String responsavelTecnico) {
        this.responsavelTecnico = responsavelTecnico;
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
