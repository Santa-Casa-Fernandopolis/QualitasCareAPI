package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ResultadoCicloLavadora;
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
@Table(name = "cme_ciclos_lavadora",
        indexes = {
                @Index(name = "ix_ciclo_lavadora_tenant_data", columnList = "tenant_id,data_hora"),
                @Index(name = "ix_ciclo_lavadora_tenant_resultado", columnList = "tenant_id,resultado")
        })
public class CicloLavadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "equipamento_descricao", length = 180)
    private String equipamentoDescricao;

    @Column(name = "numero_ciclo", length = 60)
    private String numeroCiclo;

    @Column(name = "temperatura_maxima")
    private Double temperaturaMaxima;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Column(name = "quantidade_itens")
    private Integer quantidadeItens;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ResultadoCicloLavadora resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_ciclo_lavadora_evidencias",
            joinColumns = @JoinColumn(name = "ciclo_lavadora_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getEquipamentoDescricao() { return equipamentoDescricao; }
    public void setEquipamentoDescricao(String equipamentoDescricao) { this.equipamentoDescricao = equipamentoDescricao; }
    public String getNumeroCiclo() { return numeroCiclo; }
    public void setNumeroCiclo(String numeroCiclo) { this.numeroCiclo = numeroCiclo; }
    public Double getTemperaturaMaxima() { return temperaturaMaxima; }
    public void setTemperaturaMaxima(Double temperaturaMaxima) { this.temperaturaMaxima = temperaturaMaxima; }
    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }
    public Integer getQuantidadeItens() { return quantidadeItens; }
    public void setQuantidadeItens(Integer quantidadeItens) { this.quantidadeItens = quantidadeItens; }
    public ResultadoCicloLavadora getResultado() { return resultado; }
    public void setResultado(ResultadoCicloLavadora resultado) { this.resultado = resultado; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override
    public boolean equals(Object o) { return o instanceof CicloLavadora c && Objects.equals(id, c.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
