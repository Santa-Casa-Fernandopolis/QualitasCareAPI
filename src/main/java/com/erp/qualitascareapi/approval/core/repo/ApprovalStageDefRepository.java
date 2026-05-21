package com.erp.qualitascareapi.approval.core.repo;

import com.erp.qualitascareapi.approval.core.domain.ApprovalStageDef;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalStageDefRepository extends JpaRepository<ApprovalStageDef, Long> {
    List<ApprovalStageDef> findAllByFlowDef_IdOrderByOrderAsc(Long flowDefId);
}
