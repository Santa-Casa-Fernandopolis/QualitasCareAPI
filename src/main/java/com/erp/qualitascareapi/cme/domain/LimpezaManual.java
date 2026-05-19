package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.MetodoLimpeza;
import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Audited
@Entity
@Table(name = "cme_limpezas_manuais",
        indexes = {
                @Index(name = "ix_limpeza_manual_tenant_data", columnList = "tenant_id,data_hora_inicio"),
                @Index(name = "ix_limpeza_manual_processo", columnList = "processo_id")
        })
public class LimpezaManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id")
    private ProcessoReprocessamento processo;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private User responsavel;

    @NotNull
    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    @Column(name = "produto_utilizado", length = 120)
    private String produtoUtilizado;

    @Column(name = "concentracao", length = 60)
    private String concentracao;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", length = 30)
    private MetodoLimpeza metodo;

    @Enumerated(EnumType.STRING)
    @Column(name = "conformidade", length = 20)
    private ResultadoConformidade conformidade;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_limpeza_manual_evidencias",
            joinColumns = @JoinColumn(name = "limpeza_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public ProcessoReprocessamento getProcesso() { return processo; }
    public void setProcesso(ProcessoReprocessamento processo) { this.processo = processo; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime dataHoraInicio) { this.dataHoraInicio = dataHoraInicio; }
    public LocalDateTime getDataHoraFim() { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime dataHoraFim) { this.dataHoraFim = dataHoraFim; }
    public String getProdutoUtilizado() { return produtoUtilizado; }
    public void setProdutoUtilizado(String produtoUtilizado) { this.produtoUtilizado = produtoUtilizado; }
    public String getConcentracao() { return concentracao; }
    public void setConcentracao(String concentracao) { this.concentracao = concentracao; }
    public MetodoLimpeza getMetodo() { return metodo; }
    public void setMetodo(MetodoLimpeza metodo) { this.metodo = metodo; }
    public ResultadoConformidade getConformidade() { return conformidade; }
    public void setConformidade(ResultadoConformidade conformidade) { this.conformidade = conformidade; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override
    public boolean equals(Object o) { return o instanceof LimpezaManual l && Objects.equals(id, l.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
