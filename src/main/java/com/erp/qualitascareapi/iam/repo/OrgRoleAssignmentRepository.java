package com.erp.qualitascareapi.iam.repo;

import com.erp.qualitascareapi.iam.domain.OrgRoleAssignment;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import com.erp.qualitascareapi.iam.enums.TipoSetor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrgRoleAssignmentRepository extends JpaRepository<OrgRoleAssignment, Long>,
        JpaSpecificationExecutor<OrgRoleAssignment> {

    /**
     * Retorna os assignments ativos para um papel organizacional dentro de um tenant,
     * respeitando o escopo de setor quando informado.
     *
     * <p>A lógica de escopo é inclusiva:
     * <ul>
     *   <li>Se {@code setorId} é {@code null} → retorna todos do tenant com aquele papel.</li>
     *   <li>Se {@code setorId} não é {@code null} → retorna quem está no setor especificado
     *       <b>ou</b> quem não tem setor definido (scopo global dentro do tenant).</li>
     * </ul>
     * </p>
     */
    @Query("""
            SELECT a FROM OrgRoleAssignment a
            WHERE a.tenant.id  = :tenantId
              AND a.roleType    = :roleType
              AND a.active      = true
              AND (:setorId IS NULL
                   OR a.setor IS NULL
                   OR a.setor.id = :setorId)
            """)
    List<OrgRoleAssignment> findAtivosParaEtapa(@Param("tenantId") Long tenantId,
                                                 @Param("roleType") OrgRoleType roleType,
                                                 @Param("setorId") Long setorId);

    @Query("""
            SELECT DISTINCT a FROM OrgRoleAssignment a
            JOIN FETCH a.user
            LEFT JOIN FETCH a.setor
            WHERE a.tenant.id = :tenantId
              AND a.roleType = :roleType
              AND a.active = true
              AND (a.setor IS NULL OR a.setor.tipo = :tipoSetor)
            """)
    List<OrgRoleAssignment> findAtivosPorPapelESetorTipo(@Param("tenantId") Long tenantId,
                                                          @Param("roleType") OrgRoleType roleType,
                                                          @Param("tipoSetor") TipoSetor tipoSetor);

    boolean existsByTenant_IdAndUser_IdAndRoleTypeAndActiveTrue(Long tenantId, Long userId, OrgRoleType roleType);
}
