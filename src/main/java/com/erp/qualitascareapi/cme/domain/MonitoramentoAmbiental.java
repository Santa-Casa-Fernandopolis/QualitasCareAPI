package com.erp.qualitascareapi.cme.domain;

import com.erp.qualitascareapi.cme.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Audited
@Entity
@Table(name = "cme_monitoramentos_ambientais",
        indexes = {
                @Index(name = "ix_monitoramento_tenant_data", columnList = "tenant_id,data_hora"),
                @Index(name = "ix_monitoramento_tenant_resultado", columnList = "tenant_id,resultado")
        })
public class MonitoramentoAmbiental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "local_sala", length = 120)
    private String localSala;

    @Column(name = "temperatura_celsius")
    private Double temperaturaCelsius;

    @Column(name = "umidade_relativa")
    private Double umidadeRelativa;

    @Column(name = "pressao_diferencial_pa")
    private Double pressaoDiferencialPa;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoMonitoramento resultado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 255)
    private String observacoes;

    @ManyToMany
    @JoinTable(name = "cme_monitoramento_evidencias",
            joinColumns = @JoinColumn(name = "monitoramento_id"),
            inverseJoinColumns = @JoinColumn(name = "evidencia_id"))
    private Set<EvidenciaArquivo> evidencias = new HashSet<>();

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public String getLocalSala() { return localSala; }
    public void setLocalSala(String localSala) { this.localSala = localSala; }
    public Double getTemperaturaCelsius() { return temperaturaCelsius; }
    public void setTemperaturaCelsius(Double temperaturaCelsius) { this.temperaturaCelsius = temperaturaCelsius; }
    public Double getUmidadeRelativa() { return umidadeRelativa; }
    public void setUmidadeRelativa(Double umidadeRelativa) { this.umidadeRelativa = umidadeRelativa; }
    public Double getPressaoDiferencialPa() { return pressaoDiferencialPa; }
    public void setPressaoDiferencialPa(Double pressaoDiferencialPa) { this.pressaoDiferencialPa = pressaoDiferencialPa; }
    public ResultadoMonitoramento getResultado() { return resultado; }
    public void setResultado(ResultadoMonitoramento resultado) { this.resultado = resultado; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public Set<EvidenciaArquivo> getEvidencias() { return evidencias; }
    public void setEvidencias(Set<EvidenciaArquivo> evidencias) { this.evidencias = evidencias; }

    @Override
    public boolean equals(Object o) { return o instanceof MonitoramentoAmbiental m && Objects.equals(id, m.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
