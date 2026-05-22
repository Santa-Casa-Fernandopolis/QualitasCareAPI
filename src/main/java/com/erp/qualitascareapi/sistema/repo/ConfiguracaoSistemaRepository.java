package com.erp.qualitascareapi.sistema.repo;

import com.erp.qualitascareapi.sistema.domain.ConfiguracaoSistema;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConfiguracaoSistemaRepository extends JpaRepository<ConfiguracaoSistema, Long> {

    /** Configuração global (tenant_id IS NULL) por módulo + chave. */
    Optional<ConfiguracaoSistema> findByModuloAndChaveAndTenantIdIsNull(
            ModuloConfiguracao modulo, String chave);

    /** Configuração escopada a um tenant específico por módulo + chave. */
    Optional<ConfiguracaoSistema> findByTenantIdAndModuloAndChave(
            Long tenantId, ModuloConfiguracao modulo, String chave);

    /** Todas as configurações globais de um módulo. */
    List<ConfiguracaoSistema> findByModuloAndTenantIdIsNull(ModuloConfiguracao modulo);

    /** Todas as configurações de um tenant em um módulo. */
    List<ConfiguracaoSistema> findByTenantIdAndModulo(Long tenantId, ModuloConfiguracao modulo);

    /** Todas as configurações globais. */
    List<ConfiguracaoSistema> findByTenantIdIsNull();

    boolean existsByModuloAndChaveAndTenantIdIsNull(ModuloConfiguracao modulo, String chave);

    boolean existsByTenantIdAndModuloAndChave(Long tenantId, ModuloConfiguracao modulo, String chave);
}
