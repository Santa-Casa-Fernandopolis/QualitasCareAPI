package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.EmpresaColetora;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmpresaColetorRepository extends JpaRepository<EmpresaColetora, Long> {

    Page<EmpresaColetora> findAllByTenant_Id(Long tenantId, Pageable pageable);

    List<EmpresaColetora> findAllByTenant_IdAndAtivoTrueAndDataVencimentoLicencaBefore(Long tenantId, LocalDate data);

    List<EmpresaColetora> findAllByTenant_IdAndAtivoTrueAndDataVencimentoLicencaBetween(Long tenantId, LocalDate inicio, LocalDate fim);
}
