package com.erp.qualitascareapi.approval.core.repo;

import com.erp.qualitascareapi.approval.core.domain.ApprovalStepDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovalStepDecisionRepository extends JpaRepository<ApprovalStepDecision, Long> {
    List<ApprovalStepDecision> findAllByRequest_IdOrderByDecidedAtAsc(Long requestId);
}
