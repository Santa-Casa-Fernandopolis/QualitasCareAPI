package com.erp.qualitascareapi.integracao.mv.repo;

import com.erp.qualitascareapi.integracao.mv.domain.CirurgiaAgendada;
import com.erp.qualitascareapi.integracao.mv.enums.StatusCirurgiaMv;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CirurgiaAgendadaRepository extends JpaRepository<CirurgiaAgendada, Long> {

    Optional<CirurgiaAgendada> findByTenantIdAndIdMv(Long tenantId, String idMv);

    Page<CirurgiaAgendada> findByTenantIdAndDataHoraInicioBetween(
            Long tenantId, LocalDateTime inicio, LocalDateTime fim, Pageable pageable);

    Page<CirurgiaAgendada> findByTenantIdAndStatusMv(
            Long tenantId, StatusCirurgiaMv status, Pageable pageable);

    Page<CirurgiaAgendada> findByTenantIdAndDataHoraInicioBetweenAndStatusMv(
            Long tenantId, LocalDateTime inicio, LocalDateTime fim,
            StatusCirurgiaMv status, Pageable pageable);

    Page<CirurgiaAgendada> findByTenantId(Long tenantId, Pageable pageable);
}
