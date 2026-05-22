package com.erp.qualitascareapi.notificacao.application;

import com.erp.qualitascareapi.notificacao.api.dto.NotificacaoDto;
import com.erp.qualitascareapi.notificacao.domain.Notificacao;
import com.erp.qualitascareapi.notificacao.enums.NivelNotificacao;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import com.erp.qualitascareapi.notificacao.repo.NotificacaoRepository;
import com.erp.qualitascareapi.notificacao.repo.NotificacaoSubscricaoRepository;
import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Gerencia notificações in-app e, opcionalmente, envio de e-mail.
 *
 * <h3>Visibilidade</h3>
 * <ul>
 *   <li>Notificações com {@code usuarioId = null} são visíveis para todos do tenant
 *       (ex.: alertas de temperatura).</li>
 *   <li>Notificações com {@code usuarioId != null} são visíveis somente para aquele
 *       usuário (ex.: parecer e assinatura de documentos GED).</li>
 * </ul>
 *
 * <h3>E-mail (opcional)</h3>
 * Ativado quando {@code NOTIFICACAO_EMAIL_ATIVO = true} em {@code sys_configuracoes}
 * <b>e</b> o bean {@link JavaMailSender} estiver disponível.
 * Os destinatários são obtidos via {@link NotificacaoSubscricaoRepository#findEmailsDestinatarios}.
 */
@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final NotificacaoRepository repository;
    private final NotificacaoSubscricaoRepository subscricaoRepository;
    private final ConfiguracaoService configuracaoService;

    /**
     * Injetado com {@code required = false} — se {@code spring.mail.host} não estiver
     * configurado, o bean não existe e o e-mail é silenciosamente ignorado.
     */
    private final JavaMailSender mailSender;

    public NotificacaoService(NotificacaoRepository repository,
                              NotificacaoSubscricaoRepository subscricaoRepository,
                              ConfiguracaoService configuracaoService,
                              @org.springframework.beans.factory.annotation.Autowired(required = false)
                              JavaMailSender mailSender) {
        this.repository            = repository;
        this.subscricaoRepository  = subscricaoRepository;
        this.configuracaoService   = configuracaoService;
        this.mailSender            = mailSender;
    }

    // ─── Geração ─────────────────────────────────────────────────────────────

    /**
     * Persiste uma notificação <b>global</b> (visível a todos do tenant) e,
     * se configurado, envia e-mail para os assinantes do tipo.
     */
    @Transactional
    public NotificacaoDto gerar(Long tenantId, TipoNotificacao tipo, NivelNotificacao nivel,
                                String titulo, String mensagem,
                                Long referenciaId, String referenciaTipo) {
        return gerar(tenantId, tipo, nivel, titulo, mensagem, referenciaId, referenciaTipo, null);
    }

    /**
     * Persiste uma notificação, opcionalmente direcionada a um usuário específico.
     *
     * @param usuarioId quando não-nulo, somente este usuário verá a notificação no feed
     */
    @Transactional
    public NotificacaoDto gerar(Long tenantId, TipoNotificacao tipo, NivelNotificacao nivel,
                                String titulo, String mensagem,
                                Long referenciaId, String referenciaTipo,
                                Long usuarioId) {
        Notificacao n = new Notificacao();
        n.setTenantId(tenantId);
        n.setTipo(tipo);
        n.setNivel(nivel);
        n.setTitulo(titulo);
        n.setMensagem(mensagem);
        n.setReferenciaId(referenciaId);
        n.setReferenciaTipo(referenciaTipo);
        n.setUsuarioId(usuarioId);
        n.setDataHora(LocalDateTime.now());

        Notificacao salva = repository.save(n);
        log.info("[NOTIFICACAO] {} [{}] tenant={} usuario={} — {}", nivel, tipo, tenantId, usuarioId, titulo);

        enviarEmailSeConfigurado(tenantId, tipo, titulo, mensagem, nivel);

        return toDto(salva);
    }

    // ─── Consulta ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<NotificacaoDto> listar(Long tenantId, Long usuarioId,
                                       Boolean apenasNaoLidas, Pageable pageable) {
        if (Boolean.TRUE.equals(apenasNaoLidas)) {
            return repository.findNaoLidasParaUsuario(tenantId, usuarioId, pageable).map(this::toDto);
        }
        return repository.findVisiveisParaUsuario(tenantId, usuarioId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long tenantId, Long usuarioId) {
        return repository.countNaoLidasParaUsuario(tenantId, usuarioId);
    }

    // ─── Marcação como lida ───────────────────────────────────────────────────

    @Transactional
    public NotificacaoDto marcarComoLida(Long tenantId, Long usuarioId, Long id) {
        Notificacao n = repository.findById(id)
                .filter(x -> x.getTenantId().equals(tenantId))
                .filter(x -> x.getUsuarioId() == null || x.getUsuarioId().equals(usuarioId))
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada: id=" + id));
        if (!n.isLida()) {
            n.setLida(true);
            n.setLidaEm(LocalDateTime.now());
            n = repository.save(n);
        }
        return toDto(n);
    }

    @Transactional
    public int marcarTodasComoLidas(Long tenantId, Long usuarioId) {
        return repository.marcarTodasComoLidas(tenantId, usuarioId, LocalDateTime.now());
    }

    // ─── E-mail ──────────────────────────────────────────────────────────────

    private void enviarEmailSeConfigurado(Long tenantId, TipoNotificacao tipo,
                                           String titulo, String mensagem,
                                           NivelNotificacao nivel) {
        if (mailSender == null) return;

        boolean ativo = configuracaoService.getValorBoolean(
                ModuloConfiguracao.SISTEMA, "NOTIFICACAO_EMAIL_ATIVO", false);
        if (!ativo) return;

        List<String> destinatarios = subscricaoRepository.findEmailsDestinatarios(tenantId, tipo);
        if (destinatarios.isEmpty()) return;

        String from = configuracaoService.getValor(ModuloConfiguracao.SISTEMA, "NOTIFICACAO_EMAIL_FROM");

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(from != null && !from.isBlank() ? from : "noreply@qualitascare.com");
            mail.setTo(destinatarios.toArray(new String[0]));
            mail.setSubject("[QualitasCare] " + nivel.name() + " — " + titulo);
            mail.setText(mensagem + "\n\nAcesse o sistema para mais detalhes.\n\n-- QualitasCare");
            mailSender.send(mail);
            log.info("[NOTIFICACAO-EMAIL] tipo={} → e-mail enviado para {} destinatários",
                    tipo, destinatarios.size());
        } catch (Exception e) {
            log.error("[NOTIFICACAO-EMAIL] Falha ao enviar e-mail: {}", e.getMessage(), e);
        }
    }

    // ─── Mapeamento ───────────────────────────────────────────────────────────

    private NotificacaoDto toDto(Notificacao n) {
        return new NotificacaoDto(
                n.getId(), n.getTenantId(), n.getTipo(), n.getNivel(),
                n.getTitulo(), n.getMensagem(),
                n.getReferenciaId(), n.getReferenciaTipo(),
                n.getUsuarioId(),
                n.isLida(), n.getDataHora(), n.getLidaEm()
        );
    }
}
