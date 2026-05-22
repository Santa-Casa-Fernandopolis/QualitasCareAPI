package com.erp.qualitascareapi.environmental.domain;

import com.erp.qualitascareapi.environmental.enums.ResultadoMonitoramento;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Registro individual de temperatura (e opcionalmente umidade) de uma geladeira de medicamentos/vacinas.
 *
 * Deve ser registrado com a frequência definida em {@link GeladeiraMedicamentos#getFrequenciaLeituraHoras()}.
 * O campo {@code resultado} é avaliado automaticamente comparando {@code temperaturaCelsius}
 * com os limites definidos na geladeira — mas pode ser informado manualmente pelo responsável.
 *
 * Se o resultado for NAO_CONFORME ou ALERTA, o campo {@code acaoCorretiva} deve documentar
 * a intervenção realizada (ex.: troca de equipamento, remoção de produto, acionamento técnico).
 */
@Audited
@Entity
@Table(name = "env_registros_temperatura_geladeira",
        indexes = {
                @Index(name = "ix_env_reg_gel_tenant_data", columnList = "tenant_id,data_hora"),
                @Index(name = "ix_env_reg_gel_geladeira", columnList = "geladeira_id,data_hora"),
                @Index(name = "ix_env_reg_gel_resultado", columnList = "tenant_id,resultado")
        })
public class RegistroTemperaturaGeladeira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "geladeira_id", nullable = false)
    private GeladeiraMedicamentos geladeira;

    @NotNull
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    /** Temperatura medida em graus Celsius no momento da leitura. */
    @NotNull
    @Column(name = "temperatura_celsius", nullable = false)
    private Double temperaturaCelsius;

    /** Umidade relativa (%), se o equipamento dispuser de sensor. */
    @Column(name = "umidade_relativa")
    private Double umidadeRelativa;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoMonitoramento resultado;

    /** Ação corretiva adotada quando o resultado for NAO_CONFORME ou ALERTA. */
    @Column(name = "acao_corretiva", length = 500)
    private String acaoCorretiva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsavel_id")
    private User responsavel;

    @Column(length = 255)
    private String observacoes;

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public GeladeiraMedicamentos getGeladeira() { return geladeira; }
    public void setGeladeira(GeladeiraMedicamentos geladeira) { this.geladeira = geladeira; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public Double getTemperaturaCelsius() { return temperaturaCelsius; }
    public void setTemperaturaCelsius(Double temperaturaCelsius) { this.temperaturaCelsius = temperaturaCelsius; }
    public Double getUmidadeRelativa() { return umidadeRelativa; }
    public void setUmidadeRelativa(Double umidadeRelativa) { this.umidadeRelativa = umidadeRelativa; }
    public ResultadoMonitoramento getResultado() { return resultado; }
    public void setResultado(ResultadoMonitoramento resultado) { this.resultado = resultado; }
    public String getAcaoCorretiva() { return acaoCorretiva; }
    public void setAcaoCorretiva(String acaoCorretiva) { this.acaoCorretiva = acaoCorretiva; }
    public User getResponsavel() { return responsavel; }
    public void setResponsavel(User responsavel) { this.responsavel = responsavel; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof RegistroTemperaturaGeladeira r && Objects.equals(id, r.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
