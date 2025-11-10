// services (assinaturas)
package com.erp.qualitascareapi.approval.core.services;

import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.domain.ApprovalRequest;
import com.erp.qualitascareapi.approval.core.enums.ApprovalDecision;
import com.erp.qualitascareapi.iam.domain.User;

public interface ApprovalEngine {
    ApprovalRequest start(ApprovableTarget target, User requestedBy);
    void decide(Long stepId, User user, ApprovalDecision decision, String comment);
}
