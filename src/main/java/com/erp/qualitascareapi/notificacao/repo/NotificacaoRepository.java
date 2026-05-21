package com.erp.qualitascareapi.notificacao.repo;

import com.erp.qualitascareapi.notificacao.domain.Notificacao;
import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    Page<Notificacao> findByTenantIdOrderByDataHoraDesc(Long tenantId, Pageable pageable);

    Page<Notificacao> findByTenantIdAndLidaFalseOrderByDataHoraDesc(Long tenantId, Pageable pageable);

    Page<Notificacao> findByTenantIdAndNivelOrderByDataHoraDesc(
            Long tenantId, NivelNotificacao nivel, Pageable pageable);

    /** Total de notificações não lidas — usado para badge no frontend. */
    long countByTenantIdAndLidaFalse(Long tenantId);

    /** Marca todas as não-lidas do tenant como lidas em uma só query. */
    @Modifying
    @Query("""
            UPDATE Notificacao n
            SET n.lida = true, n.lidaEm = :agora
            WHERE n.tenantId = :tenantId AND n.lida = false
            """)
    int marcarTodasComoLidas(@Param("tenantId") Long tenantId, @Param("agora") LocalDateTime agora);
}
