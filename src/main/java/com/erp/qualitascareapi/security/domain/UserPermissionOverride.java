package com.erp.qualitascareapi.security.domain;

import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.core.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.Effect;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity
@Audited
@Table(
        name = "user_permission_overrides",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_perm_override_scope",
                        columnNames = {"tenant_id","user_id","resource","action","feature","priority"}
                )
        },
        indexes = {
                @Index(name = "idx_upon_tenant_user", columnList = "tenant_id,user_id"),
                @Index(name = "idx_upon_valid_range", columnList = "valid_from,valid_until"),
                @Index(name = "idx_upon_resource_action", columnList = "resource,action")
        }
)
public class UserPermissionOverride implements ApprovableTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    private Tenant tenant;

    @Enumerated(EnumType.STRING)
    @Column(name = "resource", nullable = false, length = 40)
    private ResourceType resource;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 30)
    private Action action;

    @Column(name = "feature", length = 120)
    private String feature; // NULL = coringa

    @Enumerated(EnumType.STRING)
    @Column(name = "effect", nullable = false, length = 10)
    private Effect effect;

    @Column(name = "priority", nullable = false)
    private int priority = 100;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "valid_from")
    private LocalDateTime validFrom;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    /** Escopo opcional para resolver aprovadores por setor (approval.core) */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "target_setor_id")
    private Setor targetSetor;

    /** Metadados de submissão/aprovação (tipados) */
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "requested_by_user_id")
    private User requestedByUser;

    @Column(name = "requested_at")
    private LocalDateTime requestedAt;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "approved_by_user_id")
    private User approvedByUser;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    public UserPermissionOverride() {}
    public UserPermissionOverride(Long id) { this.id = id; }

    /* ================= ApprovableTarget ================= */

    @Override
    public Tenant getTenant() {
        return this.tenant;
    }

    @Override
    public ApprovalDomain getApprovalDomain() {
        return ApprovalDomain.USER_PERMISSION_OVERRIDE;
    }

    @Override
    public String getApprovalKey() {
        return "userOverride:" + this.id;
    }

    @Override
    public Setor getScopeSetor() {
        return this.targetSetor; // null => aprovação em nível de tenant
    }

    /* ================= Getters/Setters ================= */

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Tenant getTenantField() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public ResourceType getResource() { return resource; }
    public void setResource(ResourceType resource) { this.resource = resource; }

    public Action getAction() { return action; }
    public void setAction(Action action) { this.action = action; }

    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }

    public Effect getEffect() { return effect; }
    public void setEffect(Effect effect) { this.effect = effect; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }

    public Setor getTargetSetor() { return targetSetor; }
    public void setTargetSetor(Setor targetSetor) { this.targetSetor = targetSetor; }

    public User getRequestedByUser() { return requestedByUser; }
    public void setRequestedByUser(User requestedByUser) { this.requestedByUser = requestedByUser; }

    public LocalDateTime getRequestedAt() { return requestedAt; }
    public void setRequestedAt(LocalDateTime requestedAt) { this.requestedAt = requestedAt; }

    public User getApprovedByUser() { return approvedByUser; }
    public void setApprovedByUser(User approvedByUser) { this.approvedByUser = approvedByUser; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }
}
