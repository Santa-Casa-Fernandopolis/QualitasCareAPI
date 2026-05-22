package com.erp.qualitascareapi.pgrss.domain;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.pgrss.enums.StatusArmazenamento;
import com.erp.qualitascareapi.pgrss.enums.TipoArmazenamento;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Audited
@Entity
@Table(name = "pgrss_armazenamentos",
        indexes = {
                @Index(name = "ix_pgrss_arm_tenant_status", columnList = "tenant_id,status")
        })
public class ArmazenamentoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private GrupoResiduo grupo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_armazenamento", nullable = false, length = 20)
    private TipoArmazenamento tipoArmazenamento;

    @Column(name = "data_hora_entrada", nullable = false)
    private LocalDateTime dataHoraEntrada;

    @Column(name = "data_hora_saida")
    private LocalDateTime dataHoraSaida;

    @Column(name = "peso_estimado_kg", precision = 10, scale = 3)
    private BigDecimal pesoEstimadoKg;

    @Column(name = "responsavel_nome", nullable = false, length = 120)
    private String responsavelNome;

    @Column(length = 120)
    private String localizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusArmazenamento status = StatusArmazenamento.ARMAZENADO;

    @Column(length = 255)
    private String observacoes;

    public ArmazenamentoResiduo() {}

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public GrupoResiduo getGrupo() { return grupo; }
    public void setGrupo(GrupoResiduo grupo) { this.grupo = grupo; }
    public TipoArmazenamento getTipoArmazenamento() { return tipoArmazenamento; }
    public void setTipoArmazenamento(TipoArmazenamento tipoArmazenamento) { this.tipoArmazenamento = tipoArmazenamento; }
    public LocalDateTime getDataHoraEntrada() { return dataHoraEntrada; }
    public void setDataHoraEntrada(LocalDateTime dataHoraEntrada) { this.dataHoraEntrada = dataHoraEntrada; }
    public LocalDateTime getDataHoraSaida() { return dataHoraSaida; }
    public void setDataHoraSaida(LocalDateTime dataHoraSaida) { this.dataHoraSaida = dataHoraSaida; }
    public BigDecimal getPesoEstimadoKg() { return pesoEstimadoKg; }
    public void setPesoEstimadoKg(BigDecimal pesoEstimadoKg) { this.pesoEstimadoKg = pesoEstimadoKg; }
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String responsavelNome) { this.responsavelNome = responsavelNome; }
    public String getLocalizacao() { return localizacao; }
    public void setLocalizacao(String localizacao) { this.localizacao = localizacao; }
    public StatusArmazenamento getStatus() { return status; }
    public void setStatus(StatusArmazenamento status) { this.status = status; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof ArmazenamentoResiduo a && Objects.equals(id, a.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
