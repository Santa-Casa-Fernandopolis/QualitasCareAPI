package com.erp.qualitascareapi.environmental.domain;

import com.erp.qualitascareapi.environmental.enums.TipoDispositivoIoT;
import com.erp.qualitascareapi.iam.domain.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Dispositivo IoT (ex.: ESP32) cadastrado para enviar leituras ao sistema.
 *
 * Cada dispositivo possui uma chave de API ({@code apiKey}) única gerada no cadastro,
 * que deve ser enviada no header {@code X-Device-Key} nas requisições a {@code /api/iot/leitura}.
 *
 * O {@code tipo} determina se o sensor monitora uma geladeira ou um ambiente/sala,
 * e o sistema roteia a leitura para o registro correto automaticamente.
 */
@Audited
@Entity
@Table(name = "env_dispositivos_iot",
        indexes = {
                @Index(name = "ix_env_iot_tenant_ativo", columnList = "tenant_id,ativo"),
                @Index(name = "ix_env_iot_tenant_tipo", columnList = "tenant_id,tipo"),
                @Index(name = "ix_env_iot_api_key", columnList = "api_key", unique = true),
                @Index(name = "ix_env_iot_device_id", columnList = "device_id", unique = true)
        })
public class DispositivoIoT {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    /**
     * Identificador legível do dispositivo (ex.: "ESP32-GELAD-UTI-01").
     * Deve ser único por tenant. Definido no firmware do dispositivo.
     */
    @NotBlank
    @Column(name = "device_id", nullable = false, length = 80, unique = true)
    private String deviceId;

    /** Tipo do sensor — determina o roteamento das leituras recebidas. */
    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(nullable = false, length = 30)
    private TipoDispositivoIoT tipo;

    /**
     * Chave de API gerada pelo sistema no cadastro do dispositivo.
     * Enviada como header {@code X-Device-Key} em cada leitura.
     * Armazenada em texto para facilidade de recuperação por administradores.
     */
    @NotBlank
    @Column(name = "api_key", nullable = false, length = 80, unique = true)
    private String apiKey;

    /**
     * Geladeira monitorada por este dispositivo.
     * Obrigatório quando {@code tipo = TEMPERATURA_GELADEIRA}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geladeira_id")
    private GeladeiraMedicamentos geladeira;

    /**
     * Ambiente/sala monitorado por este dispositivo.
     * Obrigatório quando {@code tipo = MONITORAMENTO_AMBIENTAL}.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ambiente_id")
    private Ambiente ambiente;

    @Column(nullable = false)
    private boolean ativo = true;

    /** Descrição livre do dispositivo (ex.: "Sensor principal — Geladeira de Vacinas UTI"). */
    @Column(length = 200)
    private String descricao;

    /** Local físico de instalação do sensor (ex.: "Prateleira inferior, câmara interna"). */
    @Column(name = "local_instalacao", length = 120)
    private String localInstalacao;

    /** Data/hora da última leitura recebida com sucesso. Atualizada automaticamente. */
    @Column(name = "ultima_leitura")
    private LocalDateTime ultimaLeitura;

    public Long getId() { return id; }
    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public TipoDispositivoIoT getTipo() { return tipo; }
    public void setTipo(TipoDispositivoIoT tipo) { this.tipo = tipo; }
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }
    public GeladeiraMedicamentos getGeladeira() { return geladeira; }
    public void setGeladeira(GeladeiraMedicamentos geladeira) { this.geladeira = geladeira; }
    public Ambiente getAmbiente() { return ambiente; }
    public void setAmbiente(Ambiente ambiente) { this.ambiente = ambiente; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getLocalInstalacao() { return localInstalacao; }
    public void setLocalInstalacao(String localInstalacao) { this.localInstalacao = localInstalacao; }
    public LocalDateTime getUltimaLeitura() { return ultimaLeitura; }
    public void setUltimaLeitura(LocalDateTime ultimaLeitura) { this.ultimaLeitura = ultimaLeitura; }

    @Override
    public boolean equals(Object o) { return o instanceof DispositivoIoT d && Objects.equals(id, d.id); }
    @Override
    public int hashCode() { return Objects.hashCode(id); }
}
