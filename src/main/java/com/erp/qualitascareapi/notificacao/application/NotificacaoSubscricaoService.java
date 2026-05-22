package com.erp.qualitascareapi.notificacao.application;

import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.notificacao.api.dto.NotificacaoSubscricaoDto;
import com.erp.qualitascareapi.notificacao.api.dto.NotificacaoSubscricaoRequest;
import com.erp.qualitascareapi.notificacao.domain.NotificacaoSubscricao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import com.erp.qualitascareapi.notificacao.repo.NotificacaoSubscricaoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Gerencia as assinaturas de notificações por usuário.
 *
 * <h3>Regras</h3>
 * <ul>
 *   <li>Um usuário pode assinar qualquer {@link TipoNotificacao} do seu tenant.</li>
 *   <li>A combinação (usuarioId, tipo) é única — upsert idempotente.</li>
 *   <li>O campo {@code canalEmail} controla se o evento também dispara e-mail.</li>
 * </ul>
 */
@Service
public class NotificacaoSubscricaoService {

    private final NotificacaoSubscricaoRepository subscricaoRepository;
    private final UserRepository userRepository;

    public NotificacaoSubscricaoService(NotificacaoSubscricaoRepository subscricaoRepository,
                                         UserRepository userRepository) {
        this.subscricaoRepository = subscricaoRepository;
        this.userRepository       = userRepository;
    }

    // ─── Consulta ─────────────────────────────────────────────────────────────

    /** Lista todas as assinaturas de um usuário. */
    @Transactional(readOnly = true)
    public List<NotificacaoSubscricaoDto> listarPorUsuario(Long usuarioId) {
        return subscricaoRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    // ─── Upsert (criar ou atualizar) ──────────────────────────────────────────

    /**
     * Cria ou atualiza a assinatura de um tipo para um usuário.
     * Idempotente: se já existir, apenas atualiza {@code canalEmail}.
     *
     * @param tenantId   tenant do usuário
     * @param usuarioId  usuário que está assinando
     * @param request    tipo + preferência de e-mail
     */
    @Transactional
    public NotificacaoSubscricaoDto assinar(Long tenantId, Long usuarioId,
                                             NotificacaoSubscricaoRequest request) {
        validarUsuario(tenantId, usuarioId);

        NotificacaoSubscricao sub = subscricaoRepository
                .findByUsuarioIdAndTipo(usuarioId, request.tipo())
                .orElseGet(() -> {
                    NotificacaoSubscricao nova = new NotificacaoSubscricao();
                    nova.setTenantId(tenantId);
                    nova.setUsuarioId(usuarioId);
                    nova.setTipo(request.tipo());
                    return nova;
                });

        sub.setCanalEmail(request.canalEmail() != null ? request.canalEmail() : true);
        return toDto(subscricaoRepository.save(sub));
    }

    // ─── Cancelamento ─────────────────────────────────────────────────────────

    /**
     * Remove a assinatura de um tipo específico para o usuário.
     *
     * @throws EntityNotFoundException se a assinatura não existir
     */
    @Transactional
    public void cancelar(Long usuarioId, TipoNotificacao tipo) {
        if (!subscricaoRepository.existsByUsuarioIdAndTipo(usuarioId, tipo)) {
            throw new EntityNotFoundException(
                    "Subscrição não encontrada: usuarioId=" + usuarioId + ", tipo=" + tipo);
        }
        subscricaoRepository.deleteByUsuarioIdAndTipo(usuarioId, tipo);
    }

    /** Remove todas as assinaturas de um usuário (usado ao desativar/remover o usuário). */
    @Transactional
    public void cancelarTodas(Long usuarioId) {
        subscricaoRepository.deleteAllByUsuarioId(usuarioId);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private void validarUsuario(Long tenantId, Long usuarioId) {
        userRepository.findById(usuarioId)
                .filter(u -> u.getTenant().getId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado ou não pertence ao tenant: id=" + usuarioId));
    }

    private NotificacaoSubscricaoDto toDto(NotificacaoSubscricao s) {
        return new NotificacaoSubscricaoDto(
                s.getId(), s.getUsuarioId(), s.getTipo(),
                s.isCanalEmail(), s.getCriadoEm());
    }
}
