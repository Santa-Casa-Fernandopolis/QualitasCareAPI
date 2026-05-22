package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.SeveridadeNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.StatusNaoConformidade;
import com.erp.qualitascareapi.pgrss.enums.TipoNaoConformidade;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_nao_conformidades",
        indexes = {
                @Index(name = "ix_pgrss_nc_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "ix_pgrss_nc_tenant_severidade", columnList = "tenant_id,severidade")
        })
public class NaoConformidadeResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_id")
    private SetorGerador setor;

    @Column(name = "data_hora_ocorrencia", nullable = false)
    private LocalDateTime dataHoraOcorrencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private GrupoResiduo grupo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_id")
    private TipoResiduo tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_nao_conformidade", nullable = false, length = 40)
    private TipoNaoConformidade tipoNaoConformidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeveridadeNaoConformidade severidade;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(name = "acao_imediata", length = 500)
    private String acaoImediata;

    @Column(name = "area_responsavel", length = 120)
    private String areaResponsavel;

    @Column(name = "exige_plano_acao", nullable = false)
    private boolean exigePlanoAcao = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusNaoConformidade status = StatusNaoConformidade.ABERTA;

    @Column(name = "criado_por_nome", length = 120)
    private String criadoPorNome;

    public NaoConformidadeResiduo() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public SetorGerador getSetor() { return setor; }
    public void setSetor(SetorGerador setor) { this.setor = setor; }
    public LocalDateTime getDataHoraOcorrencia() { return dataHoraOcorrencia; }
    public void setDataHoraOcorrencia(LocalDateTime dataHoraOcorrencia) { this.dataHoraOcorrencia = dataHoraOcorrencia; }
    public GrupoResiduo getGrupo() { return grupo; }
    public void setGrupo(GrupoResiduo grupo) { this.grupo = grupo; }
    public TipoResiduo getTipo() { return tipo; }
    public void setTipo(TipoResiduo tipo) { this.tipo = tipo; }
    public TipoNaoConformidade getTipoNaoConformidade() { return tipoNaoConformidade; }
    public void setTipoNaoConformidade(TipoNaoConformidade tipoNaoConformidade) { this.tipoNaoConformidade = tipoNaoConformidade; }
    public SeveridadeNaoConformidade getSeveridade() { return severidade; }
    public void setSeveridade(SeveridadeNaoConformidade severidade) { this.severidade = severidade; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getAcaoImediata() { return acaoImediata; }
    public void setAcaoImediata(String acaoImediata) { this.acaoImediata = acaoImediata; }
    public String getAreaResponsavel() { return areaResponsavel; }
    public void setAreaResponsavel(String areaResponsavel) { this.areaResponsavel = areaResponsavel; }
    public boolean isExigePlanoAcao() { return exigePlanoAcao; }
    public void setExigePlanoAcao(boolean exigePlanoAcao) { this.exigePlanoAcao = exigePlanoAcao; }
    public StatusNaoConformidade getStatus() { return status; }
    public void setStatus(StatusNaoConformidade status) { this.status = status; }
    public String getCriadoPorNome() { return criadoPorNome; }
    public void setCriadoPorNome(String criadoPorNome) { this.criadoPorNome = criadoPorNome; }

    @Override
    public boolean equals(Object o) { return o instanceof NaoConformidadeResiduo n && Objects.equals(id, n.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
