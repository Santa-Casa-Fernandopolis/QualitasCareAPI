package com.erp.qualitascareapi.notificacao.domain;

import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Assinatura de um usuário para um tipo específico de notificação.
 *
 * <p>Controle fino: o farmacêutico assina apenas {@code TEMPERATURA_GELADEIRA_*},
 * a TI assina {@code DISPOSITIVO_IOT_OFFLINE}, etc.</p>
 *
 * <p>O campo {@code canalEmail} indica se, além da notificação in-app,
 * o usuário também quer receber e-mail quando o evento ocorrer.
 * Para o e-mail ser enviado, {@code NOTIFICACAO_EMAIL_ATIVO = true} em
 * {@code sys_configuracoes} <b>e</b> o usuário deve ter e-mail cadastrado.</p>
 */
@Entity
@Table(name = "notificacao_subscricoes",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_subscricao_usuario_tipo",
                columnNames = {"usuario_id", "tipo"}),
        indexes = {
                @Index(name = "ix_sub_tenant_tipo", columnList = "tenant_id,tipo"),
                @Index(name = "ix_sub_usuario",     columnList = "usuario_id")
        })
public class NotificacaoSubscricao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private TipoNotificacao tipo;

    /** {@code true} = também envia e-mail; {@code false} = somente notificação in-app. */
    @Column(name = "canal_email", nullable = false)
    private boolean canalEmail = true;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    private void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public TipoNotificacao getTipo() { return tipo; }
    public void setTipo(TipoNotificacao tipo) { this.tipo = tipo; }

    public boolean isCanalEmail() { return canalEmail; }
    public void setCanalEmail(boolean canalEmail) { this.canalEmail = canalEmail; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
}
