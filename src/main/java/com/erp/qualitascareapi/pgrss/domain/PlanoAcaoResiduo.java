package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.StatusPlanoAcao;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_planos_acao",
        indexes = {
                @Index(name = "ix_pgrss_pa_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "ix_pgrss_pa_tenant_prazo", columnList = "tenant_id,data_prazo")
        })
public class PlanoAcaoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "nao_conformidade_id", nullable = false)
    private NaoConformidadeResiduo naoConformidade;

    @Column(name = "descricao_acao", nullable = false, length = 1000)
    private String descricaoAcao;

    @Column(name = "responsavel_nome", nullable = false, length = 120)
    private String responsavelNome;

    @Column(name = "data_prazo", nullable = false)
    private LocalDate dataPrazo;

    @Column(name = "data_conclusao")
    private LocalDate dataConclusao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPlanoAcao status = StatusPlanoAcao.ABERTO;

    @Column(name = "descricao_evidencia", length = 500)
    private String descricaoEvidencia;

    public PlanoAcaoResiduo() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public NaoConformidadeResiduo getNaoConformidade() { return naoConformidade; }
    public void setNaoConformidade(NaoConformidadeResiduo naoConformidade) { this.naoConformidade = naoConformidade; }
    public String getDescricaoAcao() { return descricaoAcao; }
    public void setDescricaoAcao(String descricaoAcao) { this.descricaoAcao = descricaoAcao; }
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }
    public LocalDate getDataPrazo() { return dataPrazo; }
    public void setDataPrazo(LocalDate dataPrazo) { this.dataPrazo = dataPrazo; }
    public LocalDate getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDate dataConclusao) { this.dataConclusao = dataConclusao; }
    public StatusPlanoAcao getStatus() { return status; }
    public void setStatus(StatusPlanoAcao status) { this.status = status; }
    public String getDescricaoEvidencia() { return descricaoEvidencia; }
    public void setDescricaoEvidencia(String descricaoEvidencia) { this.descricaoEvidencia = descricaoEvidencia; }

    @Override
    public boolean equals(Object o) { return o instanceof PlanoAcaoResiduo p && Objects.equals(id, p.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
