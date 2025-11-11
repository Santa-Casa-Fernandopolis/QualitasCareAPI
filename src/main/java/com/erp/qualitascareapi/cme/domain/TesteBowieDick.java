package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.*;
import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** ---------------- TesteBowieDick ---------------- */
@Audited
@Entity
@Table(name = "cme_bowie_dick",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_bd_autoclave_data", columnNames = {"autoclave_id","data_execucao"})
        },
        indexes = {
                @Index(name = "ix_bd_autoclave_data", columnList = "autoclave_id,data_execucao")
        })
public class TesteBowieDick {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @NotNull @Column(name = "data_execucao", nullable = false)
    private LocalDate dataExecucao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoConformidade resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executado_por_id")
    private User executadoPor;

    @Column(length = 800)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_bowie_dick_evidencias",
            joinColumns = @JoinColumn(name = "bowie_dick_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Autoclave getAutoclave() { return autoclave; }
    public void setAutoclave(Autoclave autoclave) { this.autoclave = autoclave; }
    public LocalDate getDataExecucao() { return dataExecucao; }
    public void setDataExecucao(LocalDate dataExecucao) { this.dataExecucao = dataExecucao; }
    public ResultadoConformidade getResultado() { return resultado; }
    public void setResultado(ResultadoConformidade resultado) { this.resultado = resultado; }
    public User getExecutadoPor() { return executadoPor; }
    public void setExecutadoPor(User executadoPor) { this.executadoPor = executadoPor; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override public boolean equals(Object o){ return o instanceof TesteBowieDick t && Objects.equals(id, t.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
