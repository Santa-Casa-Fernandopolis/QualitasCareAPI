package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.*;
import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.core.domain.KitVersion;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/** ---------------- CicloEsterilizacao ---------------- */
@Audited
@Entity
@Table(name = "cme_ciclos",
        indexes = {
                @Index(name = "ix_ciclo_tenant_autoclave_inicio", columnList = "tenant_id,autoclave_id,inicio"),
                @Index(name = "ix_ciclo_tenant_status", columnList = "tenant_id,status"),
                @Index(name = "ix_ciclo_tenant_lote", columnList = "tenant_id,lote_etiqueta_id")
        })
public class CicloEsterilizacao implements ApprovableTarget {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "autoclave_id", nullable = false)
    private Autoclave autoclave;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_etiqueta_id")
    private LoteEtiqueta loteEtiqueta;

    @NotNull @Column(nullable = false)
    private LocalDateTime inicio;

    private LocalDateTime fim;

    @Min(0)
    private Integer duracaoMinutos;

    private Double temperaturaMaxima;
    private Double pressaoMaxima;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CicloStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liberado_por_id")
    private User liberadoPor;

    @Column(length = 800)
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_executor_id")
    private Setor setorExecutor;

    // Gates
    @NotNull @Column(nullable = false) private Boolean parametrosOk = Boolean.FALSE;
    @NotNull @Column(nullable = false) private Boolean iqOk         = Boolean.FALSE;
    @NotNull @Column(nullable = false) private Boolean ibExigido    = Boolean.FALSE;
    @NotNull @Column(nullable = false) private Boolean ibOk         = Boolean.FALSE;

    // Evidências (N:N)
    @ManyToMany
    @JoinTable(name = "cme_ciclo_evidencias",
            joinColumns = @JoinColumn(name = "ciclo_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    // ApprovableTarget
    @Override public Tenant getTenant() { return tenant; }
    @Override public ApprovalDomain getApprovalDomain() { return ApprovalDomain.CICLO_ESTERILIZACAO; }
    @Override public String getApprovalKey() { return id == null ? null : "cmeCycle:" + id; }
    @Override public Setor getScopeSetor() { return setorExecutor; }

    // getters/setters/equals/hashCode
    public Long getId() { return id; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public Autoclave getAutoclave() { return autoclave; }
    public void setAutoclave(Autoclave autoclave) { this.autoclave = autoclave; }
    public LoteEtiqueta getLoteEtiqueta() { return loteEtiqueta; }
    public void setLoteEtiqueta(LoteEtiqueta loteEtiqueta) { this.loteEtiqueta = loteEtiqueta; }
    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }
    public LocalDateTime getFim() { return fim; }
    public void setFim(LocalDateTime fim) { this.fim = fim; }
    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer d) { this.duracaoMinutos = d; }
    public Double getTemperaturaMaxima() { return temperaturaMaxima; }
    public void setTemperaturaMaxima(Double t) { this.temperaturaMaxima = t; }
    public Double getPressaoMaxima() { return pressaoMaxima; }
    public void setPressaoMaxima(Double p) { this.pressaoMaxima = p; }
    public CicloStatus getStatus() { return status; }
    public void setStatus(CicloStatus status) { this.status = status; }
    public User getLiberadoPor() { return liberadoPor; }
    public void setLiberadoPor(User liberadoPor) { this.liberadoPor = liberadoPor; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Setor getSetorExecutor() { return setorExecutor; }
    public void setSetorExecutor(Setor setorExecutor) { this.setorExecutor = setorExecutor; }
    public Boolean getParametrosOk() { return parametrosOk; }
    public void setParametrosOk(Boolean parametrosOk) { this.parametrosOk = parametrosOk; }
    public Boolean getIqOk() { return iqOk; }
    public void setIqOk(Boolean iqOk) { this.iqOk = iqOk; }
    public Boolean getIbExigido() { return ibExigido; }
    public void setIbExigido(Boolean ibExigido) { this.ibExigido = ibExigido; }
    public Boolean getIbOk() { return ibOk; }
    public void setIbOk(Boolean ibOk) { this.ibOk = ibOk; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override public boolean equals(Object o){ return o instanceof CicloEsterilizacao c && Objects.equals(id, c.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
