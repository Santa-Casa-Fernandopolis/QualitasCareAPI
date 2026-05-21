package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Tabela de custos de tratamento por tipo de resíduo e empresa coletora.
 * Permite rastrear o custo financeiro da gestão de resíduos por período.
 */
@Audited
@Entity
@Table(name = "pgrss_custos_tratamento",
        indexes = {
                @Index(name = "ix_pgrss_ct_tenant_empresa", columnList = "tenant_id,empresa_coletora_id"),
                @Index(name = "ix_pgrss_ct_tenant_tipo", columnList = "tenant_id,tipo_residuo_id"),
                @Index(name = "ix_pgrss_ct_vigencia", columnList = "vigencia_inicio,vigencia_fim")
        })
public class CustoTratamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_coletora_id", nullable = false)
    private EmpresaColetora empresaColetora;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "tipo_residuo_id", nullable = false)
    private TipoResiduo tipoResiduo;

    /** Valor cobrado por quilograma de resíduo. */
    @NotNull
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal valorPorKg;

    @Column(length = 10, nullable = false)
    private String moeda = "BRL";

    @NotNull
    @Column(nullable = false)
    private LocalDate vigenciaInicio;

    private LocalDate vigenciaFim;

    @Column(length = 500)
    private String observacoes;

    public CustoTratamento() {}

    public Long getId() { return id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public EmpresaColetora getEmpresaColetora() { return empresaColetora; }
    public void setEmpresaColetora(EmpresaColetora empresaColetora) { this.empresaColetora = empresaColetora; }

    public TipoResiduo getTipoResiduo() { return tipoResiduo; }
    public void setTipoResiduo(TipoResiduo tipoResiduo) { this.tipoResiduo = tipoResiduo; }

    public BigDecimal getValorPorKg() { return valorPorKg; }
    public void setValorPorKg(BigDecimal valorPorKg) { this.valorPorKg = valorPorKg; }

    public String getMoeda() { return moeda; }
    public void setMoeda(String moeda) { this.moeda = moeda; }

    public LocalDate getVigenciaInicio() { return vigenciaInicio; }
    public void setVigenciaInicio(LocalDate vigenciaInicio) { this.vigenciaInicio = vigenciaInicio; }

    public LocalDate getVigenciaFim() { return vigenciaFim; }
    public void setVigenciaFim(LocalDate vigenciaFim) { this.vigenciaFim = vigenciaFim; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof CustoTratamento c && Objects.equals(id, c.id); }

    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
