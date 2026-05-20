package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ResultadoConformidade;
import com.erp.qualitascareapi.cme.enums.TipoSecagem;
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
 * Registra a etapa de secagem do material após a limpeza.
 * Aplica-se a ambos os fluxos: cirúrgico (após ultrassônica) e inalatório (após banho químico).
 * Tipo MAQUINA_LAVADORA indica uso de lavadora termodesinfetora com ciclo de secagem integrado.
 */
@Audited
@Entity
@Table(name = "cme_secagens",
        indexes = {
                @Index(name = "ix_secagem_tenant_processo", columnList = "tenant_id,processo_id"),
                @Index(name = "ix_secagem_tenant_tipo",     columnList = "tenant_id,tipo_secagem"),
                @Index(name = "ix_secagem_tenant_inicio",   columnList = "tenant_id,data_hora_inicio")
        })
public class SecagemMaterial {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processo_id")
    private ProcessoReprocessamento processo;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_secagem", nullable = false, length = 30)
    private TipoSecagem tipoSecagem;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id", nullable = false)
    private User responsavel;

    @NotNull
    @Column(name = "data_hora_inicio", nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_fim")
    private LocalDateTime dataHoraFim;

    // Preenchido quando tipoSecagem = MAQUINA_LAVADORA ou ESTUFA
    @Column(name = "equipamento_descricao", length = 180)
    private String equipamentoDescricao;

    @Enumerated(EnumType.STRING)
    @Column(name = "conformidade", length = 20)
    private ResultadoConformidade conformidade;

    @Column(length = 800)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_secagem_evidencias",
            joinColumns        = @JoinColumn(name = "secagem_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public Long getId()                                        { return id; }
    public Tenant getTenant()                                  { return tenant; }
    public void setTenant(Tenant tenant)                       { this.tenant = tenant; }
    public ProcessoReprocessamento getProcesso()               { return processo; }
    public void setProcesso(ProcessoReprocessamento processo)  { this.processo = processo; }
    public TipoSecagem getTipoSecagem()                        { return tipoSecagem; }
    public void setTipoSecagem(TipoSecagem tipoSecagem)        { this.tipoSecagem = tipoSecagem; }
    public User getResponsavel()                               { return responsavel; }
    public void setResponsavel(User responsavel)               { this.responsavel = responsavel; }
    public LocalDateTime getDataHoraInicio()                   { return dataHoraInicio; }
    public void setDataHoraInicio(LocalDateTime d)             { this.dataHoraInicio = d; }
    public LocalDateTime getDataHoraFim()                      { return dataHoraFim; }
    public void setDataHoraFim(LocalDateTime d)                { this.dataHoraFim = d; }
    public String getEquipamentoDescricao()                    { return equipamentoDescricao; }
    public void setEquipamentoDescricao(String s)              { this.equipamentoDescricao = s; }
    public ResultadoConformidade getConformidade()             { return conformidade; }
    public void setConformidade(ResultadoConformidade c)       { this.conformidade = c; }
    public String getObservacoes()                             { return observacoes; }
    public void setObservacoes(String observacoes)             { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias()               { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> e)         { this.evidencias = e; }

    @Override public boolean equals(Object o){ return o instanceof SecagemMaterial s && Objects.equals(id, s.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
