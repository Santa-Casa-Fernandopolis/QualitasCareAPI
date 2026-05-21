package com.erp.qualitascareapi.ged.repo;

import com.erp.qualitascareapi.ged.domain.Document;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document> {
    Optional<Document> findByIdAndTenant_Id(Long id, Long tenantId);
    Optional<Document> findByTenant_IdAndCodigoIgnoreCase(Long tenantId, String codigo);
    long countByTenant_Id(Long tenantId);
    long countByTenant_IdAndStatus(Long tenantId, DocumentStatus status);
    long countByTenant_IdAndVersaoAtualIsNotNull(Long tenantId);
    long countByTenant_IdAndVersaoAtualIsNull(Long tenantId);
    long countByTenant_IdAndExigeTreinamentoTrue(Long tenantId);
    long countByTenant_IdAndNecessitaParecerJuridicoTrue(Long tenantId);
    long countByTenant_IdAndDataVigenciaFimBefore(Long tenantId, LocalDate date);
    long countByTenant_IdAndDataVigenciaFimBetween(Long tenantId, LocalDate start, LocalDate end);

    @Query("select d.status, count(d) from Document d where d.tenant.id = :tenantId group by d.status")
    List<Object[]> countByStatus(@Param("tenantId") Long tenantId);

    @Query("select d.tipo, count(d) from Document d where d.tenant.id = :tenantId group by d.tipo")
    List<Object[]> countByType(@Param("tenantId") Long tenantId);

    @Query("""
            select coalesce(s.nome, 'Sem setor'), count(d)
            from Document d
            left join d.setorResponsavel s
            where d.tenant.id = :tenantId
            group by s.nome
            order by count(d) desc
            """)
    List<Object[]> countByDepartment(@Param("tenantId") Long tenantId, Pageable pageable);

    @Query("""
            select d
            from Document d
            left join fetch d.setorResponsavel
            left join fetch d.versaoAtual
            where d.tenant.id = :tenantId
              and d.dataVigenciaFim is not null
              and d.dataVigenciaFim <= :limitDate
            order by d.dataVigenciaFim asc
            """)
    List<Document> findCriticalValidity(@Param("tenantId") Long tenantId,
                                        @Param("limitDate") LocalDate limitDate,
                                        Pageable pageable);
}
