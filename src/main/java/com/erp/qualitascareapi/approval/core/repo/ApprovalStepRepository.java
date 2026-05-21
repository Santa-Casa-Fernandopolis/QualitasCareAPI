package com.erp.qualitascareapi.approval.core.repo;

import com.erp.qualitascareapi.approval.core.domain.ApprovalStep;
import com.erp.qualitascareapi.approval.core.enums.ApprovalStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {
    List<ApprovalStep> findAllByRequest_IdOrderByStageOrderAsc(Long requestId);
    Optional<ApprovalStep> findFirstByRequest_IdAndStatusOrderByStageOrderAsc(Long requestId, ApprovalStepStatus status);
    Optional<ApprovalStep> findByRequest_IdAndStageCode(Long requestId, String stageCode);
}
