package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.RecebimentoStatus;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.Setor;
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
@Table(name = "cme_recebimentos",
        indexes = {
                @Index(name = "ix_recebimento_tenant_data", columnList = "tenant_id,data_hora"),
                @Index(name = "ix_recebimento_tenant_status", columnList = "tenant_id,status")
        })
public class RecebimentoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_origem_id")
    private Setor setorOrigem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(name = "quantidade_itens")
    private Integer quantidadeItens;

    @Column(name = "condicao_descricao", length = 255)
    private String condicaoDescricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RecebimentoStatus status;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_recebimento_evidencias",
            joinColumns = @JoinColumn(name = "recebimento_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public Setor getSetorOrigem() { return setorOrigem; }
    public void setSetorOrigem(Setor setorOrigem) { this.setorOrigem = setorOrigem; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public Integer getQuantidadeItens() { return quantidadeItens; }
    public void setQuantidadeItens(Integer quantidadeItens) { this.quantidadeItens = quantidadeItens; }
    public String getCondicaoDescricao() { return condicaoDescricao; }
    public void setCondicaoDescricao(String condicaoDescricao) { this.condicaoDescricao = condicaoDescricao; }
    public RecebimentoStatus getStatus() { return status; }
    public void setStatus(RecebimentoStatus status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override
    public boolean equals(Object o) { return o instanceof RecebimentoMaterial r && Objects.equals(id, r.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
