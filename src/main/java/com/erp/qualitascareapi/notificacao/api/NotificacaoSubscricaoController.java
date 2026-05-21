package com.erp.qualitascareapi.notificacao.api;

import com.erp.qualitascareapi.notificacao.api.dto.NotificacaoSubscricaoDto;
import com.erp.qualitascareapi.notificacao.api.dto.NotificacaoSubscricaoRequest;
import com.erp.qualitascareapi.notificacao.application.NotificacaoSubscricaoService;
import com.erp.qualitascareapi.notificacao.enums.TipoNotificacao;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gerencia as assinaturas de notificações por usuário.
 *
 * <ul>
 *   <li>{@code GET  /api/notificacoes/subscricoes/{usuarioId}}        — lista assinaturas</li>
 *   <li>{@code PUT  /api/notificacoes/subscricoes/{usuarioId}}        — cria/atualiza assinatura</li>
 *   <li>{@code DELETE /api/notificacoes/subscricoes/{usuarioId}/{tipo}} — cancela assinatura</li>
 * </ul>
 *
 * <p>Um usuário pode gerenciar apenas as próprias assinaturas.
 * Administradores (permissão {@code NOTIFICACAO / UPDATE}) podem gerenciar
 * as assinaturas de qualquer usuário do tenant.</p>
 */
@RestController
@RequestMapping("/api/notificacoes/subscricoes")
public class NotificacaoSubscricaoController {

    private final NotificacaoSubscricaoService subscricaoService;
    private final TenantScopeGuard tenantScopeGuard;

    public NotificacaoSubscricaoController(NotificacaoSubscricaoService subscricaoService,
                                            TenantScopeGuard tenantScopeGuard) {
        this.subscricaoService = subscricaoService;
        this.tenantScopeGuard  = tenantScopeGuard;
    }

    /**
     * Lista todas as assinaturas ativas de um usuário.
     */
    @GetMapping("/{usuarioId}")
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.READ)
    public List<NotificacaoSubscricaoDto> listar(@PathVariable Long usuarioId) {
        return subscricaoService.listarPorUsuario(usuarioId);
    }

    /**
     * Cria ou atualiza a assinatura de um tipo de notificação para o usuário.
     * Idempotente — chamar duas vezes com os mesmos dados produz o mesmo resultado.
     *
     * <pre>
     * PUT /api/notificacoes/subscricoes/42
     * { "tipo": "TEMPERATURA_GELADEIRA_NAO_CONFORME", "canalEmail": true }
     * </pre>
     */
    @PutMapping("/{usuarioId}")
    @ResponseStatus(HttpStatus.OK)
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.UPDATE)
    public NotificacaoSubscricaoDto assinar(@PathVariable Long usuarioId,
                                             @Validated @RequestBody NotificacaoSubscricaoRequest request) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return subscricaoService.assinar(tenantId, usuarioId, request);
    }

    /**
     * Cancela a assinatura de um tipo específico para o usuário.
     */
    @DeleteMapping("/{usuarioId}/{tipo}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.UPDATE)
    public void cancelar(@PathVariable Long usuarioId,
                         @PathVariable TipoNotificacao tipo) {
        subscricaoService.cancelar(usuarioId, tipo);
    }
}
