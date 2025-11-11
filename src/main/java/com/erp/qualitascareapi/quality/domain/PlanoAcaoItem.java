package com.erp.qualitascareapi.quality.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Audited
@Table(name = "nc_plano_acao_item")
public class PlanoAcaoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "nc_id", nullable = false)
    private NaoConformidadeCME nc;

    @Column(length = 300)
    private String oQue;

    @Column(length = 300)
    private String porQue;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "quem_user_id")
    private User quem;

    @Column(length = 120)
    private String onde;

    private LocalDate quando;

    @Column(length = 600)
    private String como;

    private BigDecimal quanto;

    private LocalDate prazo;

    @Column(length = 60)
    private String statusExecucao;

    @Column(length = 1000)
    private String evidencias;

    public PlanoAcaoItem() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public NaoConformidadeCME getNc() { return nc; }
    public void setNc(NaoConformidadeCME nc) { this.nc = nc; }

    public String getOQue() { return oQue; }
    public void setOQue(String oQue) { this.oQue = oQue; }

    public String getPorQue() { return porQue; }
    public void setPorQue(String porQue) { this.porQue = porQue; }

    public User getQuem() { return quem; }
    public void setQuem(User quem) { this.quem = quem; }

    public String getOnde() { return onde; }
    public void setOnde(String onde) { this.onde = onde; }

    public LocalDate getQuando() { return quando; }
    public void setQuando(LocalDate quando) { this.quando = quando; }

    public String getComo() { return como; }
    public void setComo(String como) { this.como = como; }

    public BigDecimal getQuanto() { return quanto; }
    public void setQuanto(BigDecimal quanto) { this.quanto = quanto; }

    public LocalDate getPrazo() { return prazo; }
    public void setPrazo(LocalDate prazo) { this.prazo = prazo; }

    public String getStatusExecucao() { return statusExecucao; }
    public void setStatusExecucao(String statusExecucao) { this.statusExecucao = statusExecucao; }

    public String getEvidencias() { return evidencias; }
    public void setEvidencias(String evidencias) { this.evidencias = evidencias; }
}
