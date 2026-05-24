package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ProcessoStatus;
import com.erp.qualitascareapi.cme.enums.TipoFluxoCME;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

@Audited
@Entity
@Table(name = "cme_processos_reprocessamento",
        uniqueConstraints = @UniqueConstraint(name = "uk_processo_tenant_numero",
                columnNames = {"tenant_id", "numero_processo"}),
        indexes = {
                @Index(name = "ix_processo_tenant_status",   columnList = "tenant_id,status"),
                @Index(name = "ix_processo_tenant_tipo",     columnList = "tenant_id,tipo_fluxo"),
                @Index(name = "ix_processo_tenant_abertura", columnList = "tenant_id,data_abertura")
        })
public class ProcessoReprocessamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank
    @Column(name = "numero_processo", nullable = false, length = 60)
    private String numeroProcesso;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_fluxo", nullable = false, length = 20)
    private TipoFluxoCME tipoFluxo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fluxo_processo_id")
    private CmeFluxoProcesso fluxoProcesso;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProcessoStatus status = ProcessoStatus.ABERTO;

    @NotNull
    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recebimento_id")
    private RecebimentoMaterial recebimento;

    @Column(length = 255)
    private String observacoes;

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getNumeroProcesso() { return numeroProcesso; }
    public void setNumeroProcesso(String numeroProcesso) { this.numeroProcesso = numeroProcesso; }
    public TipoFluxoCME getTipoFluxo() { return tipoFluxo; }
    public void setTipoFluxo(TipoFluxoCME tipoFluxo) { this.tipoFluxo = tipoFluxo; }
    public CmeFluxoProcesso getFluxoProcesso() { return fluxoProcesso; }
    public void setFluxoProcesso(CmeFluxoProcesso fluxoProcesso) { this.fluxoProcesso = fluxoProcesso; }
    public ProcessoStatus getStatus() { return status; }
    public void setStatus(ProcessoStatus status) { this.status = status; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }
    public LocalDateTime getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(LocalDateTime dataConclusao) { this.dataConclusao = dataConclusao; }
    public RecebimentoMaterial getRecebimento() { return recebimento; }
    public void setRecebimento(RecebimentoMaterial recebimento) { this.recebimento = recebimento; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof ProcessoReprocessamento p && Objects.equals(id, p.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
