package com.erp.qualitascareapi.notificacao.repo;

import com.erp.qualitascareapi.notificacao.domain.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    /**
     * Lista todas as notificações visíveis para o usuário:
     * globais ({@code usuarioId IS NULL}) + pessoais do próprio usuário.
     */
    @Query("""
            SELECT n FROM Notificacao n
            WHERE n.tenantId = :tenantId
              AND (n.usuarioId IS NULL OR n.usuarioId = :usuarioId)
            ORDER BY n.dataHora DESC
            """)
    Page<Notificacao> findVisiveisParaUsuario(@Param("tenantId") Long tenantId,
                                              @Param("usuarioId") Long usuarioId,
                                              Pageable pageable);

    /**
     * Lista notificações não lidas visíveis para o usuário.
     */
    @Query("""
            SELECT n FROM Notificacao n
            WHERE n.tenantId = :tenantId
              AND n.lida = false
              AND (n.usuarioId IS NULL OR n.usuarioId = :usuarioId)
            ORDER BY n.dataHora DESC
            """)
    Page<Notificacao> findNaoLidasParaUsuario(@Param("tenantId") Long tenantId,
                                               @Param("usuarioId") Long usuarioId,
                                               Pageable pageable);

    /**
     * Contagem de não lidas — usada para badge do sininho.
     */
    @Query("""
            SELECT COUNT(n) FROM Notificacao n
            WHERE n.tenantId = :tenantId
              AND n.lida = false
              AND (n.usuarioId IS NULL OR n.usuarioId = :usuarioId)
            """)
    long countNaoLidasParaUsuario(@Param("tenantId") Long tenantId,
                                   @Param("usuarioId") Long usuarioId);

    /**
     * Marca como lidas em massa somente as notificações visíveis para o usuário.
     */
    @Modifying
    @Query("""
            UPDATE Notificacao n
            SET n.lida = true, n.lidaEm = :agora
            WHERE n.tenantId = :tenantId
              AND n.lida = false
              AND (n.usuarioId IS NULL OR n.usuarioId = :usuarioId)
            """)
    int marcarTodasComoLidas(@Param("tenantId") Long tenantId,
                              @Param("usuarioId") Long usuarioId,
                              @Param("agora") LocalDateTime agora);
}
