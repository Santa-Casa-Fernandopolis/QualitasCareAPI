package com.erp.qualitascareapi.approval.core.domain;

import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;

@Entity
@Audited
@Table(name = "approval_stage_defs",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_stage_flow_order", columnNames = {"flow_def_id", "stage_order"}),
                @UniqueConstraint(name = "uq_stage_flow_code", columnNames = {"flow_def_id", "stage_code"})
        },
        indexes = @Index(name = "idx_stage_flow_order", columnList = "flow_def_id,stage_order"))
public class ApprovalStageDef {

    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="flow_def_id", nullable=false)
    private ApprovalFlowDef flowDef;

    @Column(name = "stage_order", nullable = false)
    private Integer order;

    @Column(name = "stage_code", nullable = false, length = 80)
    private String stageCode;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=60)
    private OrgRoleType requiredRole;

    @Column(nullable=false)
    private Boolean scopeByTargetSetor = Boolean.FALSE;

    @Column(nullable=false)
    private Integer minApprovers = 1;

    @Column(nullable=false)
    private Boolean dualApproval = Boolean.FALSE;

    public ApprovalStageDef() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

