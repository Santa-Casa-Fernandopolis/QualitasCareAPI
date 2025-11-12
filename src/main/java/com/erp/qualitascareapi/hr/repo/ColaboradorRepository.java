package com.erp.qualitascareapi.hr.repo;

import com.erp.qualitascareapi.hr.domain.Colaborador;
import com.erp.qualitascareapi.hr.enums.ColaboradorStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    @EntityGraph(attributePaths = {"tenant", "setor", "cargo", "usuarioSistema"})
    Page<Colaborador> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"tenant", "setor", "cargo", "usuarioSistema"})
    Page<Colaborador> findAllByTenant_Id(Long tenantId, Pageable pageable);

    @EntityGraph(attributePaths = {"tenant", "setor", "cargo", "usuarioSistema"})
    Page<Colaborador> findAllByTenant_IdAndStatus(Long tenantId, ColaboradorStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"tenant", "setor", "cargo", "usuarioSistema"})
    Page<Colaborador> findAllByStatus(ColaboradorStatus status, Pageable pageable);

    Optional<Colaborador> findByTenant_IdAndMatriculaIgnoreCase(Long tenantId, String matricula);

    Optional<Colaborador> findByTenant_IdAndCpf(Long tenantId, String cpf);

    @Override
    @EntityGraph(attributePaths = {"tenant", "setor", "cargo", "usuarioSistema"})
    Optional<Colaborador> findById(Long id);
}
