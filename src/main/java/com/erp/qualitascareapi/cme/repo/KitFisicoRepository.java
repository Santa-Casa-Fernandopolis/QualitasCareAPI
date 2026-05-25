package com.erp.qualitascareapi.cme.repo;

import com.erp.qualitascareapi.cme.domain.KitFisico;
import com.erp.qualitascareapi.cme.enums.IdentificacaoFisicaStatus;
import com.erp.qualitascareapi.cme.enums.LoteStatus;
import com.erp.qualitascareapi.cme.enums.StatusAprovacaoCme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KitFisicoRepository extends JpaRepository<KitFisico, Long> {
    Page<KitFisico> findAllByTenant_Id(Long tenantId, Pageable pageable);
    Page<KitFisico> findAllByTenant_IdAndIdentificadorUnicoContainingIgnoreCase(Long tenantId, String identificadorUnico, Pageable pageable);
    Optional<KitFisico> findByTenant_IdAndIdentificadorUnicoIgnoreCase(Long tenantId, String identificadorUnico);
    boolean existsByTenant_IdAndIdentificadorUnicoIgnoreCase(Long tenantId, String identificadorUnico);

    @Query("""
            SELECT k
              FROM KitFisico k
             WHERE k.tenant.id = :tenantId
               AND k.ativo = true
               AND k.status = :kitStatus
               AND k.statusAprovacao = :aprovacaoStatus
               AND k.kit IS NOT NULL
               AND k.kitVersaoAtual IS NOT NULL
               AND (:identificador IS NULL OR LOWER(k.identificadorUnico) LIKE LOWER(CONCAT('%', :identificador, '%')))
               AND NOT EXISTS (
                    SELECT 1
                      FROM LoteEtiqueta l
                     WHERE l.kitFisico = k
                       AND l.tenant.id = :tenantId
                       AND l.status IN :activeStatuses
               )
            """)
    Page<KitFisico> findDisponiveisEntrada(
            @Param("tenantId") Long tenantId,
            @Param("identificador") String identificador,
            @Param("kitStatus") IdentificacaoFisicaStatus kitStatus,
            @Param("aprovacaoStatus") StatusAprovacaoCme aprovacaoStatus,
            @Param("activeStatuses") List<LoteStatus> activeStatuses,
            Pageable pageable);
}
