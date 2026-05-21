package com.erp.qualitascareapi.notificacao.repo;

import com.erp.qualitascareapi.notificacao.domain.NotificacaoSubscricao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificacaoSubscricaoRepository extends JpaRepository<NotificacaoSubscricao, Long> {

    List<NotificacaoSubscricao> findByUsuarioId(Long usuarioId);

    List<NotificacaoSubscricao> findByTenantIdAndTipo(Long tenantId, TipoNotificacao tipo);

    Optional<NotificacaoSubscricao> findByUsuarioIdAndTipo(Long usuarioId, TipoNotificacao tipo);

    boolean existsByUsuarioIdAndTipo(Long usuarioId, TipoNotificacao tipo);

    @Modifying
    @Query("DELETE FROM NotificacaoSubscricao s WHERE s.usuarioId = :usuarioId AND s.tipo = :tipo")
    void deleteByUsuarioIdAndTipo(@Param("usuarioId") Long usuarioId,
                                  @Param("tipo") TipoNotificacao tipo);

    @Modifying
    @Query("DELETE FROM NotificacaoSubscricao s WHERE s.usuarioId = :usuarioId")
    void deleteAllByUsuarioId(@Param("usuarioId") Long usuarioId);

    /**
     * Retorna os e-mails dos usuários do tenant que assinaram o tipo informado
     * com {@code canalEmail = true} e que possuem e-mail cadastrado.
     * Usado pelo {@code NotificacaoService} para montar a lista de destinatários.
     */
    @Query("""
            SELECT u.email
            FROM NotificacaoSubscricao s
            JOIN com.erp.qualitascareapi.iam.domain.User u ON u.id = s.usuarioId
            WHERE s.tenantId = :tenantId
              AND s.tipo     = :tipo
              AND s.canalEmail = true
              AND u.email IS NOT NULL
              AND u.email <> ''
            """)
    List<String> findEmailsDestinatarios(@Param("tenantId") Long tenantId,
                                          @Param("tipo") TipoNotificacao tipo);
}
