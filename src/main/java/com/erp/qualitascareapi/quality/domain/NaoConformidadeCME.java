package com.erp.qualitascareapi.quality.domain;

import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.core.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.quality.enums.NaoConformidadeSeveridade;
import com.erp.qualitascareapi.quality.enums.NaoConformidadeStatus;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Audited
@Table(name = "nc_cme")
public class NaoConformidadeCME implements ApprovableTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tipo_id", nullable = false)
    private TipoNaoConformidade tipo;

    @Column(nullable = false, length = 180)
    private String titulo;

    @Column(columnDefinition = "text")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NaoConformidadeSeveridade severidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NaoConformidadeStatus status;

    private LocalDate dataAbertura;
    private LocalDate dataEncerramento;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "responsavel_id")
    private User responsavel;

    /** NOVOS CAMPOS PARA INTEGRAÇÃO COM APPROVAL / ONA */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "setor_origem_id")
    private Setor setorOrigem;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "setor_responsavel_id")
    private Setor setorResponsavel;

    @Column(length = 1000)
    private String planoAcaoResumo;

    @ManyToMany
    @JoinTable(
            name = "nc_cme_evidencias",
            joinColumns = @JoinColumn(name = "nc_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id")
    )
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public NaoConformidadeCME() {}

    // ===== ApprovableTarget =====
    @Override
    public Tenant getTenant() { return this.tenant; }

    @Override
    public ApprovalDomain getApprovalDomain() {
        return ApprovalDomain.NAO_CONFORMIDADE;
    }

    @Override
    public String getApprovalKey() {
        return "ncCME:" + this.id;
    }

    @Override
    public Setor getScopeSetor() {
        // se existir um setor responsável pelo plano de ação, use-o para etapas do tipo GERENCIA_SETOR
        return (this.setorResponsavel != null) ? this.setorResponsavel : this.setorOrigem;
    }

    // ===== Getters/Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenantField() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public TipoNaoConformidade getTipo() { return tipo; }
    public void setTipo(TipoNaoConformidade tipo) { this.tipo = tipo; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public NaoConformidadeSeveridade getSeveridade() { return severidade; }
    public void setSeveridade(NaoConformidadeSeveridade severidade) { this.severidade = severidade; }

    public NaoConformidadeStatus getStatus() { return status; }
    public void setStatus(NaoConformidadeStatus status) { this.status = status; }

    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }

    public LocalDate getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDate dataEncerramento) { this.dataEncerramento = dataEncerramento; }

    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }

    public Setor getSetorOrigem() { return setorOrigem; }
    public void setSetorOrigem(Setor setorOrigem) { this.setorOrigem = setorOrigem; }

    public Setor getSetorResponsavel() { return setorResponsavel; }
    public void setSetorResponsavel(Setor setorResponsavel) { this.setorResponsavel = setorResponsavel; }

    public String getPlanoAcaoResumo() { return planoAcaoResumo; }
    public void setPlanoAcaoResumo(String planoAcaoResumo) { this.planoAcaoResumo = planoAcaoResumo; }

    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }
}
