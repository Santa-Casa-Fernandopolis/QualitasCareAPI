package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.util.Objects;

/**
 * Tipo específico de resíduo hospitalar vinculado a um {@link GrupoResiduo}.
 */
@Audited
@Entity
@Table(name = "pgrss_tipos_residuo",
        uniqueConstraints = @UniqueConstraint(name = "uk_pgrss_tr_tenant_codigo",
                columnNames = {"tenant_id", "codigo"}),
        indexes = {
                @Index(name = "ix_pgrss_tr_tenant_grupo", columnList = "tenant_id,grupo_residuo_id"),
                @Index(name = "ix_pgrss_tr_tenant_ativo", columnList = "tenant_id,ativo")
        })
public class TipoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_residuo_id", nullable = false)
    private GrupoResiduo grupoResiduo;

    @NotBlank
    @Column(nullable = false, length = 40)
    private String codigo;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PericulosidadeResiduo periculosidade;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private EstadoFisicoResiduo estadoFisico;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoAcondicionamento tipoAcondicionamento;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoTratamento tipoTratamento;

    @Enumerated(EnumType.STRING)
    @Column(length = 35)
    private TipoDestinacaoFinal tipoDestinacaoFinal;

    /** Indica se este tipo exige licença ambiental específica da empresa coletora. */
    @Column(nullable = false)
    private Boolean requerLicenca = Boolean.FALSE;

    @Column(nullable = false)
    private Boolean ativo = Boolean.TRUE;

    public TipoResiduo() {}

    public Long getId() { return id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public GrupoResiduo getGrupoResiduo() { return grupoResiduo; }
    public void setGrupoResiduo(GrupoResiduo grupoResiduo) { this.grupoResiduo = grupoResiduo; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public PericulosidadeResiduo getPericulosidade() { return periculosidade; }
    public void setPericulosidade(PericulosidadeResiduo periculosidade) { this.periculosidade = periculosidade; }

    public EstadoFisicoResiduo getEstadoFisico() { return estadoFisico; }
    public void setEstadoFisico(EstadoFisicoResiduo estadoFisico) { this.estadoFisico = estadoFisico; }

    public TipoAcondicionamento getTipoAcondicionamento() { return tipoAcondicionamento; }
    public void setTipoAcondicionamento(TipoAcondicionamento tipoAcondicionamento) { this.tipoAcondicionamento = tipoAcondicionamento; }

    public TipoTratamento getTipoTratamento() { return tipoTratamento; }
    public void setTipoTratamento(TipoTratamento tipoTratamento) { this.tipoTratamento = tipoTratamento; }

    public TipoDestinacaoFinal getTipoDestinacaoFinal() { return tipoDestinacaoFinal; }
    public void setTipoDestinacaoFinal(TipoDestinacaoFinal tipoDestinacaoFinal) { this.tipoDestinacaoFinal = tipoDestinacaoFinal; }

    public Boolean getRequerLicenca() { return requerLicenca; }
    public void setRequerLicenca(Boolean requerLicenca) { this.requerLicenca = requerLicenca; }

    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof TipoResiduo t && Objects.equals(id, t.id); }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
