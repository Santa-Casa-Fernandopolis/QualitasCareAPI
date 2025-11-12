package com.erp.qualitascareapi.iam.repo;

import com.erp.qualitascareapi.iam.domain.OrgRoleAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrgRoleAssignmentRepository extends JpaRepository<OrgRoleAssignment, Long>,
        JpaSpecificationExecutor<OrgRoleAssignment> {
}
