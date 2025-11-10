package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/** ---------------- HigienizacaoUltrassonica ---------------- */
@Audited
@Entity
@Table(name = "cme_hig_ultrassonica",
        indexes = {
                @Index(name = "ix_hig_ultra_tenant_data", columnList = "tenant_id,data_realizacao"),
                @Index(name = "ix_hig_ultra_tenant_resp", columnList = "tenant_id,responsavel_id")
        })
public class HigienizacaoUltrassonica {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @Column(name = "data_realizacao", nullable = false)
    private LocalDate dataRealizacao;

    @Column(length = 160)
    private String equipamentoDescricao;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 800)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_hig_ultra_evidencias",
            joinColumns = @JoinColumn(name = "hig_ultra_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public LocalDate getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(LocalDate dataRealizacao) { this.dataRealizacao = dataRealizacao; }
    public String getEquipamentoDescricao() { return equipamentoDescricao; }
    public void setEquipamentoDescricao(String equipamentoDescricao) { this.equipamentoDescricao = equipamentoDescricao; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override public boolean equals(Object o){ return o instanceof HigienizacaoUltrassonica h && Objects.equals(id, h.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
