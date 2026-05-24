package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.TesteBowieDick;
import com.erp.qualitascareapi.cme.enums.BowieDickStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TesteBowieDickRepository extends JpaRepository<TesteBowieDick, Long> {
    Page<TesteBowieDick> findAllByAutoclave_Tenant_Id(Long tenantId, Pageable pageable);
    Optional<TesteBowieDick> findByAutoclave_IdAndDataExecucao(Long autoclaveId, LocalDate dataExecucao);

    @Query("""
            SELECT t FROM TesteBowieDick t
            JOIN FETCH t.autoclave a
            JOIN FETCH a.tenant
            LEFT JOIN FETCH t.validador
            WHERE t.status = :status
            """)
    List<TesteBowieDick> findAllByStatusWithAutoclaveAndValidador(@Param("status") BowieDickStatus status);
}
