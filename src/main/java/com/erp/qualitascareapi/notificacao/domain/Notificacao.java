package com.erp.qualitascareapi.notificacao.domain;

import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Notificação de sistema gerada automaticamente quando uma leitura ambiental
 * ou de geladeira resulta em ALERTA ou NAO_CONFORME.
 *
 * <p>Visível para qualquer usuário do tenant com permissão {@code ENV_MONITORAMENTO / READ}.
 * Pode ser marcada como lida individualmente ou em massa.</p>
 *
 * <p>Opcionalmente, o {@code NotificacaoService} também envia e-mail
 * quando {@code NOTIFICACAO_EMAIL_ATIVO = true} em {@code sys_configuracoes}.</p>
 */
@Entity
@Table(name = "notificacoes",
        indexes = {
                @Index(name = "ix_not_tenant_lida_data", columnList = "tenant_id,lida,data_hora"),
                @Index(name = "ix_not_tenant_nivel",     columnList = "tenant_id,nivel"),
                @Index(name = "ix_not_referencia",       columnList = "referencia_tipo,referencia_id")
        })
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private TipoNotificacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private NivelNotificacao nivel;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensagem;

    /**
     * ID do registro que gerou a notificação
     * (ex.: id do RegistroTemperaturaGeladeira ou MonitoramentoAmbiental).
     */
    @Column(name = "referencia_id")
    private Long referenciaId;

    /**
     * Tipo do registro de origem: {@code "GELADEIRA"}, {@code "AMBIENTE"}, {@code "IOT"}.
     */
    @Column(name = "referencia_tipo", length = 30)
    private String referenciaTipo;

    @Column(nullable = false)
    private boolean lida = false;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "lida_em")
    private LocalDateTime lidaEm;

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public TipoNotificacao getTipo() { return tipo; }
    public void setTipo(TipoNotificacao tipo) { this.tipo = tipo; }

    public NivelNotificacao getNivel() { return nivel; }
    public void setNivel(NivelNotificacao nivel) { this.nivel = nivel; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public Long getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Long referenciaId) { this.referenciaId = referenciaId; }

    public String getReferenciaTipo() { return referenciaTipo; }
    public void setReferenciaTipo(String referenciaTipo) { this.referenciaTipo = referenciaTipo; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public LocalDateTime getLidaEm() { return lidaEm; }
    public void setLidaEm(LocalDateTime lidaEm) { this.lidaEm = lidaEm; }
}
