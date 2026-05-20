package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.*;
import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Representa um ciclo de esterilização em autoclave.
 * Um ciclo pode conter múltiplos lotes (LoteEtiqueta aponta para este ciclo via FK).
 */
@Audited
@Entity
@Table(name = "cme_ciclos",
        indexes = {
                @Index(name = "ix_ciclo_tenant_autoclave_inicio", columnList = "tenant_id,autoclave_id,inicio"),
                @Index(name = "ix_ciclo_tenant_status",           columnList = "tenant_id,status"),
                @Index(name = "ix_ciclo_tenant_processo",         columnList = "tenant_id,processo_id")
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
    @JoinColumn(name = "processo_id")
    private ProcessoReprocessamento processo;

    // Lotes são recuperados pela relação inversa em LoteEtiqueta (mappedBy="cicloEsterilizacao")
    @OneToMany(mappedBy = "cicloEsterilizacao", fetch = FetchType.LAZY)
    private Set<LoteEtiqueta> lotes = new HashSet<>();

    @NotNull @Column(nullable = false)
    private LocalDateTime inicio;

    private LocalDateTime fim;

    @Min(0)
    private Integer duracaoMinutos;

    private Double temperaturaMaxima;
    private Double pressaoMaxima;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CicloStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "liberado_por_id")
    private User liberadoPor;

    @Column(length = 800)
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setor_executor_id")
    private Setor setorExecutor;

    @NotNull @Column(nullable = false) private Boolean parametrosOk = Boolean.FALSE;
    @NotNull @Column(nullable = false) private Boolean iqOk         = Boolean.FALSE;
    @NotNull @Column(nullable = false) private Boolean ibExigido    = Boolean.FALSE;
    @NotNull @Column(nullable = false) private Boolean ibOk         = Boolean.FALSE;

    @ManyToMany
    @JoinTable(name = "cme_ciclo_evidencias",
            joinColumns        = @JoinColumn(name = "ciclo_id"),
            inverseJoinColumns = @JoinColumn(name = "arquivo_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public ProcessoReprocessamento getProcesso() { return processo; }
    public void setProcesso(ProcessoReprocessamento processo) { this.processo = processo; }

    @Override public Tenant getTenant()               { return tenant; }
    @Override public ApprovalDomain getApprovalDomain() { return ApprovalDomain.CICLO_ESTERILIZACAO; }
    @Override public String getApprovalKey()          { return id == null ? null : "cmeCycle:" + id; }
    @Override public Setor getScopeSetor()            { return setorExecutor; }

    public Long getId()                               { return id; }
    public void setTenant(Tenant tenant)              { this.tenant = tenant; }
    public Autoclave getAutoclave()                   { return autoclave; }
    public void setAutoclave(Autoclave autoclave)     { this.autoclave = autoclave; }
    public Set<LoteEtiqueta> getLotes()               { return lotes; }
    public void setLotes(Set<LoteEtiqueta> lotes)     { this.lotes = lotes; }
    public LocalDateTime getInicio()                  { return inicio; }
    public void setInicio(LocalDateTime inicio)       { this.inicio = inicio; }
    public LocalDateTime getFim()                     { return fim; }
    public void setFim(LocalDateTime fim)             { this.fim = fim; }
    public Integer getDuracaoMinutos()                { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer d)          { this.duracaoMinutos = d; }
    public Double getTemperaturaMaxima()              { return temperaturaMaxima; }
    public void setTemperaturaMaxima(Double t)        { this.temperaturaMaxima = t; }
    public Double getPressaoMaxima()                  { return pressaoMaxima; }
    public void setPressaoMaxima(Double p)            { this.pressaoMaxima = p; }
    public CicloStatus getStatus()                    { return status; }
    public void setStatus(CicloStatus status)         { this.status = status; }
    public User getLiberadoPor()                      { return liberadoPor; }
    public void setLiberadoPor(User liberadoPor)      { this.liberadoPor = liberadoPor; }
    public String getObservacoes()                    { return observacoes; }
    public void setObservacoes(String observacoes)    { this.observacoes = observacoes; }
    public Setor getSetorExecutor()                   { return setorExecutor; }
    public void setSetorExecutor(Setor s)             { this.setorExecutor = s; }
    public Boolean getParametrosOk()                  { return parametrosOk; }
    public void setParametrosOk(Boolean b)            { this.parametrosOk = b; }
    public Boolean getIqOk()                          { return iqOk; }
    public void setIqOk(Boolean b)                    { this.iqOk = b; }
    public Boolean getIbExigido()                     { return ibExigido; }
    public void setIbExigido(Boolean b)               { this.ibExigido = b; }
    public Boolean getIbOk()                          { return ibOk; }
    public void setIbOk(Boolean b)                    { this.ibOk = b; }
    public Set<EvidenciaArquivo> getEvidencias()      { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> e){ this.evidencias = e; }

    @Override public boolean equals(Object o){ return o instanceof CicloEsterilizacao c && Objects.equals(id, c.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
