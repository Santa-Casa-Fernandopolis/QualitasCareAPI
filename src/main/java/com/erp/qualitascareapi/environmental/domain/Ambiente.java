package com.erp.qualitascareapi.environmental.domain;

import com.erp.qualitascareapi.environmental.enums.TipoAmbiente;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.util.Objects;

/**
 * Cadastro de ambientes/salas monitorados do hospital.
 * Permite registrar locais específicos com nome, localização e parâmetros
 * alvo (temperatura, umidade, pressão diferencial) para avaliação de conformidade
 * nos monitoramentos ambientais e leituras de dispositivos IoT.
 *
 * Exemplos: "Sala de Esterilização B2 — CME", "UTI Adulta — Ala Norte", "Bloco Cirúrgico 3".
 */
@Audited
@Entity
@Table(name = "env_ambientes",
        indexes = {
                @Index(name = "ix_env_amb_tenant_ativo", columnList = "tenant_id,ativo"),
                @Index(name = "ix_env_amb_tenant_tipo", columnList = "tenant_id,tipo_ambiente")
        })
public class Ambiente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /** Nome descritivo do ambiente (ex.: "Sala de Lavagem CME — Bloco B"). */
    @NotBlank
    @Column(nullable = false, length = 120)
    private String nome;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "tipo_ambiente", nullable = false, length = 40)
    private TipoAmbiente tipoAmbiente;

    /** Bloco físico ou ala do hospital (ex.: "Bloco B", "Ala Norte"). */
    @Column(length = 60)
    private String bloco;

    /** Andar onde o ambiente está localizado (ex.: "2º Andar", "Térreo"). */
    @Column(length = 30)
    private String andar;

    /** Setor/área responsável (ex.: "CME", "UTI", "Farmácia"). */
    @Column(length = 80)
    private String setor;

    // --- Parâmetros alvo para avaliação automática de conformidade ---

    /** Temperatura mínima aceitável (°C). Null = sem limite inferior configurado. */
    @Column(name = "temperatura_min_celsius")
    private Double temperaturaMinCelsius;

    /** Temperatura máxima aceitável (°C). Null = sem limite superior configurado. */
    @Column(name = "temperatura_max_celsius")
    private Double temperaturaMaxCelsius;

    /** Umidade relativa mínima aceitável (%). */
    @Column(name = "umidade_min_percentual")
    private Double umidadeMinPercentual;

    /** Umidade relativa máxima aceitável (%). */
    @Column(name = "umidade_max_percentual")
    private Double umidadeMaxPercentual;

    /** Pressão diferencial mínima aceitável (Pa). Usado em salas limpas e blocos cirúrgicos. */
    @Column(name = "pressao_min_pa")
    private Double pressaoMinPa;

    /** Pressão diferencial máxima aceitável (Pa). */
    @Column(name = "pressao_max_pa")
    private Double pressaoMaxPa;

    @Column(nullable = false)
    private boolean ativo = true;

    @Column(length = 255)
    private String observacoes;

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public TipoAmbiente getTipoAmbiente() { return tipoAmbiente; }
    public void setTipoAmbiente(TipoAmbiente tipoAmbiente) { this.tipoAmbiente = tipoAmbiente; }
    public String getBloco() { return bloco; }
    public void setBloco(String bloco) { this.bloco = bloco; }
    public String getAndar() { return andar; }
    public void setAndar(String andar) { this.andar = andar; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
    public Double getTemperaturaMinCelsius() { return temperaturaMinCelsius; }
    public void setTemperaturaMinCelsius(Double v) { this.temperaturaMinCelsius = v; }
    public Double getTemperaturaMaxCelsius() { return temperaturaMaxCelsius; }
    public void setTemperaturaMaxCelsius(Double v) { this.temperaturaMaxCelsius = v; }
    public Double getUmidadeMinPercentual() { return umidadeMinPercentual; }
    public void setUmidadeMinPercentual(Double v) { this.umidadeMinPercentual = v; }
    public Double getUmidadeMaxPercentual() { return umidadeMaxPercentual; }
    public void setUmidadeMaxPercentual(Double v) { this.umidadeMaxPercentual = v; }
    public Double getPressaoMinPa() { return pressaoMinPa; }
    public void setPressaoMinPa(Double v) { this.pressaoMinPa = v; }
    public Double getPressaoMaxPa() { return pressaoMaxPa; }
    public void setPressaoMaxPa(Double v) { this.pressaoMaxPa = v; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    @Override
    public boolean equals(Object o) { return o instanceof Ambiente a && Objects.equals(id, a.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
