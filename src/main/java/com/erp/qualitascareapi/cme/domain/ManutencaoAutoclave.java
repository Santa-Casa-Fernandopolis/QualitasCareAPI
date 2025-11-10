package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ManutencaoStatus;
import com.erp.qualitascareapi.cme.enums.ManutencaoTipo;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/** ---------------- ManutencaoAutoclave ---------------- */
@Audited
@Entity
@Table(name = "cme_manutencoes",
        indexes = {
                @Index(name = "ix_manutencao_autoclave_status", columnList = "autoclave_id,status"),
                @Index(name = "ix_manutencao_autoclave_agendamento", columnList = "autoclave_id,data_agendamento")
        })
public class ManutencaoAutoclave {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManutencaoTipo tipo;

    @NotNull @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ManutencaoStatus status;

    @Column(name = "data_agendamento")
    private LocalDate dataAgendamento;

    @Column(name = "data_execucao")
    private LocalDate dataExecucao;

    @Column(length = 160)
    private String responsavelTecnico;

    @Column(length = 800)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_manutencao_evidencias",
            joinColumns = @JoinColumn(name = "manutencao_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Autoclave getAutoclave() { return autoclave; }
    public void setAutoclave(Autoclave autoclave) { this.autoclave = autoclave; }
    public ManutencaoTipo getTipo() { return tipo; }
    public void setTipo(ManutencaoTipo tipo) { this.tipo = tipo; }
    public ManutencaoStatus getStatus() { return status; }
    public void setStatus(ManutencaoStatus status) { this.status = status; }
    public LocalDate getDataAgendamento() { return dataAgendamento; }
    public void setDataAgendamento(LocalDate dataAgendamento) { this.dataAgendamento = dataAgendamento; }
    public LocalDate getDataExecucao() { return dataExecucao; }
    public void setDataExecucao(LocalDate dataExecucao) { this.dataExecucao = dataExecucao; }
    public String getResponsavelTecnico() { return responsavelTecnico; }
    public void setResponsavelTecnico(String responsavelTecnico) { this.responsavelTecnico = responsavelTecnico; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override public boolean equals(Object o){ return o instanceof ManutencaoAutoclave m && Objects.equals(id, m.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
