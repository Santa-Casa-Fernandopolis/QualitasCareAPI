package com.erp.qualitascareapi.sistema.domain;

import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import com.erp.qualitascareapi.sistema.enums.TipoValorConfiguracao;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

/**
 * Parâmetro de configuração do sistema, persistido na tabela {@code sys_configuracoes}.
 *
 * <p>Cada registro representa uma chave dentro de um módulo, opcionalmente escopada a um
 * tenant específico. Registros com {@code tenantId = null} são configurações globais do sistema.</p>
 *
 * <p>Valores do tipo {@link TipoValorConfiguracao#SECRET} são armazenados cifrados com AES
 * e mascarados nas respostas da API.</p>
 *
 * <h3>Unicidade</h3>
 * <ul>
 *   <li>Global: {@code (modulo, chave)} onde {@code tenant_id IS NULL}</li>
 *   <li>Por tenant: {@code (tenant_id, modulo, chave)} onde {@code tenant_id IS NOT NULL}</li>
 * </ul>
 */
@Audited
@Entity
@Table(name = "sys_configuracoes",
        indexes = {
                @Index(name = "ix_sys_conf_modulo", columnList = "modulo"),
                @Index(name = "ix_sys_conf_tenant_modulo", columnList = "tenant_id,modulo")
        })
public class ConfiguracaoSistema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Null = configuração global (vale para todos os tenants). */
    @Column(name = "tenant_id")
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "modulo", nullable = false, length = 50)
    private ModuloConfiguracao modulo;

    @Column(name = "chave", nullable = false, length = 100)
    private String chave;

    /** Armazenado cifrado quando {@code tipoValor = SECRET}. */
    @Column(name = "valor", columnDefinition = "TEXT")
    private String valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_valor", nullable = false, length = 20)
    private TipoValorConfiguracao tipoValor;

    @Column(name = "descricao", length = 255)
    private String descricao;

    /** Quando {@code false} a chave não pode ser alterada pela API. */
    @Column(name = "editavel", nullable = false)
    private boolean editavel = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }

    public Long getTenantId() { return tenantId; }
    public void setTenantId(Long tenantId) { this.tenantId = tenantId; }

    public ModuloConfiguracao getModulo() { return modulo; }
    public void setModulo(ModuloConfiguracao modulo) { this.modulo = modulo; }

    public String getChave() { return chave; }
    public void setChave(String chave) { this.chave = chave; }

    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }

    public TipoValorConfiguracao getTipoValor() { return tipoValor; }
    public void setTipoValor(TipoValorConfiguracao tipoValor) { this.tipoValor = tipoValor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public boolean isEditavel() { return editavel; }
    public void setEditavel(boolean editavel) { this.editavel = editavel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
