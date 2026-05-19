package com.erp.qualitascareapi.core.repo;

import com.erp.qualitascareapi.core.domain.ExameCultura;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExameCulturaRepository extends JpaRepository<ExameCultura, Long> {
    Page<ExameCultura> findAllByTenantId(Long tenantId, Pageable pageable);
}
