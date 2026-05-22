package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_custos_tratamento",
        indexes = {
                @Index(name = "ix_pgrss_ct_tenant_grupo", columnList = "tenant_id,grupo_id")
        })
public class CustoTratamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoResiduo grupo;

    @Column(name = "custo_por_kg", nullable = false, precision = 10, scale = 4)
    private BigDecimal custoPorKg;

    @Column(name = "data_inicio_vigencia", nullable = false)
    private LocalDate dataInicioVigencia;

    @Column(name = "data_fim_vigencia")
    private LocalDate dataFimVigencia;

    @Column(nullable = false)
    private boolean ativo = true;

    public CustoTratamento() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public GrupoResiduo getGrupo() { return grupo; }
    public void setGrupo(GrupoResiduo grupo) { this.grupo = grupo; }
    public BigDecimal getCustoPorKg() { return custoPorKg; }
    public void setCustoPorKg(BigDecimal custoPorKg) { this.custoPorKg = custoPorKg; }
    public LocalDate getDataInicioVigencia() { return dataInicioVigencia; }
    public void setDataInicioVigencia(LocalDate dataInicioVigencia) { this.dataInicioVigencia = dataInicioVigencia; }
    public LocalDate getDataFimVigencia() { return dataFimVigencia; }
    public void setDataFimVigencia(LocalDate dataFimVigencia) { this.dataFimVigencia = dataFimVigencia; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public boolean equals(Object o) { return o instanceof CustoTratamento c && Objects.equals(id, c.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
