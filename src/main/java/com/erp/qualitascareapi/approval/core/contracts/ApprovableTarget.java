// approval.core.contracts
package com.erp.qualitascareapi.approval.core.contracts;

import com.erp.qualitascareapi.approval.core.enums.ApprovalDomain;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;

public interface ApprovableTarget {
    Tenant getTenant();
    ApprovalDomain getApprovalDomain();
    /** Ex.: "docVersion:123", "trainPlan:45" */
    String getApprovalKey();
    /** Pode retornar null quando não aplicável */
    Setor getScopeSetor();
}
