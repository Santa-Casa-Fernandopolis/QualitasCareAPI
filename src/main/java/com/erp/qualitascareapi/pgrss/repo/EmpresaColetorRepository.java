package com.erp.qualitascareapi.pgrss.repo;

import com.erp.qualitascareapi.pgrss.domain.EmpresaColetora;
import com.erp.qualitascareapi.pgrss.enums.LicencaAmbientalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmpresaColetorRepository extends JpaRepository<EmpresaColetora, Long> {

    Page<EmpresaColetora> findAllByTenant_Id(Long tenantId, Pageable pageable);

    Page<EmpresaColetora> findAllByTenant_IdAndAtivo(Long tenantId, Boolean ativo, Pageable pageable);

    List<EmpresaColetora> findAllByTenant_IdAndLicencaStatus(Long tenantId, LicencaAmbientalStatus status);

    /** Busca empresas com licença vencendo até a data informada. */
    List<EmpresaColetora> findAllByTenant_IdAndAtivoTrueAndLicencaVencimentoBefore(Long tenantId, LocalDate data);

    boolean existsByTenant_IdAndCnpj(Long tenantId, String cnpj);
}
