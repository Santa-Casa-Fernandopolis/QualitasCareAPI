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
 * <h3>In-app</h3>
 * Toda notificação é sempre persistida na tabela {@code notificacoes} e ficará
 * visível para usuários autorizados do tenant via {@code GET /api/notificacoes}.
 *
 * <h3>E-mail (opcional)</h3>
 * Ativado quando {@code NOTIFICACAO_EMAIL_ATIVO = true} em {@code sys_configuracoes}
 * (módulo {@code SISTEMA}) <b>e</b> o bean {@link JavaMailSender} estiver disponível
 * (requer {@code spring.mail.*} configurado).
 *
 * <p>Parâmetros de e-mail em {@code sys_configuracoes} (módulo {@code SISTEMA}):
 * <ul>
 *   <li>{@code NOTIFICACAO_EMAIL_ATIVO}       — {@code true} / {@code false}</li>
 *   <li>{@code NOTIFICACAO_EMAIL_FROM}        — remetente (ex.: {@code qualitascare@hospital.com})</li>
 *   <li>{@code NOTIFICACAO_EMAIL_DESTINATARIOS} — lista de e-mails separada por vírgula</li>
 * </ul>
 * </p>
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
     * Persiste uma notificação in-app e, se e-mail estiver configurado, envia para os destinatários.
     *
     * @param tenantId       tenant do evento
     * @param tipo           tipo da notificação
     * @param nivel          nível de severidade
     * @param titulo         título curto (max 200 chars)
     * @param mensagem       descrição detalhada (max 500 chars)
     * @param referenciaId   ID do registro de origem (pode ser null)
     * @param referenciaTipo tipo do registro de origem: "GELADEIRA", "AMBIENTE", "IOT"
     */
    @Transactional
    public NotificacaoDto gerar(Long tenantId, TipoNotificacao tipo, NivelNotificacao nivel,
                                String titulo, String mensagem,
                                Long referenciaId, String referenciaTipo) {
        Notificacao n = new Notificacao();
        n.setTenantId(tenantId);
        n.setTipo(tipo);
        n.setNivel(nivel);
        n.setTitulo(titulo);
        n.setMensagem(mensagem);
        n.setReferenciaId(referenciaId);
        n.setReferenciaTipo(referenciaTipo);
        n.setDataHora(LocalDateTime.now());

        Notificacao salva = repository.save(n);
        log.info("[NOTIFICACAO] {} [{}] tenant={} — {}", nivel, tipo, tenantId, titulo);

        enviarEmailSeConfigurado(tenantId, tipo, titulo, mensagem, nivel);

        return toDto(salva);
    }

    // ─── Consulta ─────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<NotificacaoDto> listar(Long tenantId, Boolean apenasNaoLidas, Pageable pageable) {
        if (Boolean.TRUE.equals(apenasNaoLidas)) {
            return repository.findByTenantIdAndLidaFalseOrderByDataHoraDesc(tenantId, pageable).map(this::toDto);
        }
        return repository.findByTenantIdOrderByDataHoraDesc(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(Long tenantId) {
        return repository.countByTenantIdAndLidaFalse(tenantId);
    }

    // ─── Marcação como lida ───────────────────────────────────────────────────

    @Transactional
    public NotificacaoDto marcarComoLida(Long tenantId, Long id) {
        Notificacao n = repository.findById(id)
                .filter(x -> x.getTenantId().equals(tenantId))
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada: id=" + id));
        if (!n.isLida()) {
            n.setLida(true);
            n.setLidaEm(LocalDateTime.now());
            n = repository.save(n);
        }
        return toDto(n);
    }

    @Transactional
    public int marcarTodasComoLidas(Long tenantId) {
        return repository.marcarTodasComoLidas(tenantId, LocalDateTime.now());
    }

    // ─── E-mail ──────────────────────────────────────────────────────────────

    /**
     * Envia e-mail para todos os usuários do tenant que assinaram {@code tipo} com
     * {@code canalEmail = true} e têm e-mail cadastrado.
     *
     * <p>Requisitos para o envio:
     * <ol>
     *   <li>{@code JavaMailSender} disponível (requer {@code spring.mail.host}).</li>
     *   <li>{@code NOTIFICACAO_EMAIL_ATIVO = true} em {@code sys_configuracoes}.</li>
     *   <li>Pelo menos um usuário com assinatura ativa para o tipo informado.</li>
     * </ol>
     * </p>
     */
    private void enviarEmailSeConfigurado(Long tenantId, TipoNotificacao tipo,
                                           String titulo, String mensagem,
                                           NivelNotificacao nivel) {
        if (mailSender == null) return;

        boolean ativo = configuracaoService.getValorBoolean(
                ModuloConfiguracao.SISTEMA, "NOTIFICACAO_EMAIL_ATIVO", false);
        if (!ativo) return;

        // Busca e-mails dos assinantes com canal e-mail ativo para este tipo
        List<String> destinatarios = subscricaoRepository
                .findEmailsDestinatarios(tenantId, tipo);

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
            // Falha no e-mail não deve impedir a persistência da notificação in-app
            log.error("[NOTIFICACAO-EMAIL] Falha ao enviar e-mail: {}", e.getMessage(), e);
        }
    }

    // ─── Mapeamento ───────────────────────────────────────────────────────────

    private NotificacaoDto toDto(Notificacao n) {
        return new NotificacaoDto(
                n.getId(), n.getTenantId(), n.getTipo(), n.getNivel(),
                n.getTitulo(), n.getMensagem(),
                n.getReferenciaId(), n.getReferenciaTipo(),
                n.isLida(), n.getDataHora(), n.getLidaEm()
        );
    }
}
