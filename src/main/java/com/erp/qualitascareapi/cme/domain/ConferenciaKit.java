package com.erp.qualitascareapi.cme.domain;

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

/**
 * Inspeção visual e conferência do kit após secagem, antes da embalagem.
 * Verifica completude dos instrumentos, ausência de corrosão, danos ou sujidade residual.
 * Etapa obrigatória para materiais cirúrgicos (RDC 15/2012); recomendada para inalatórios.
 */
@Audited
@Entity
@Table(name = "cme_conferencias_kit",
        indexes = {
                @Index(name = "ix_conferencia_tenant_processo",  columnList = "tenant_id,processo_id"),
                @Index(name = "ix_conferencia_tenant_data",      columnList = "tenant_id,data_hora_conferencia")
        })
public class ConferenciaKit {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "data_hora_conferencia", nullable = false)
    private LocalDateTime dataHoraConferencia;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "conformidade", nullable = false, length = 20)
    private ResultadoConformidade conformidade;

    // Lista livre dos instrumentos ausentes ou danificados (ex.: "pinça Kelly nº2 - ausente")
    @Column(name = "itens_nao_conformes", length = 1000)
    private String itensNaoConformes;

    @Column(length = 800)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_conferencia_evidencias",
            joinColumns        = @JoinColumn(name = "conferencia_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public Long getId()                                        { return id; }
    public Tenant getTenant()                                  { return tenant; }
    public void setTenant(Tenant tenant)                       { this.tenant = tenant; }
    public ProcessoReprocessamento getProcesso()               { return processo; }
    public void setProcesso(ProcessoReprocessamento processo)  { this.processo = processo; }
    public User getResponsavel()                               { return responsavel; }
    public void setResponsavel(User responsavel)               { this.responsavel = responsavel; }
    public LocalDateTime getDataHoraConferencia()              { return dataHoraConferencia; }
    public void setDataHoraConferencia(LocalDateTime d)        { this.dataHoraConferencia = d; }
    public ResultadoConformidade getConformidade()             { return conformidade; }
    public void setConformidade(ResultadoConformidade c)       { this.conformidade = c; }
    public String getItensNaoConformes()                       { return itensNaoConformes; }
    public void setItensNaoConformes(String s)                 { this.itensNaoConformes = s; }
    public String getObservacoes()                             { return observacoes; }
    public void setObservacoes(String observacoes)             { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias()               { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> e)         { this.evidencias = e; }

    @Override public boolean equals(Object o){ return o instanceof ConferenciaKit c && Objects.equals(id, c.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
