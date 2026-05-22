package com.erp.qualitascareapi.notificacao.api;

import com.erp.qualitascareapi.notificacao.api.dto.NotificacaoDto;
import com.erp.qualitascareapi.notificacao.application.NotificacaoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.app.AuthContext;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Endpoints de notificações in-app.
 *
 * <p>Cada operação filtra automaticamente as notificações visíveis para o usuário
 * autenticado: globais do tenant ({@code usuarioId IS NULL}) + pessoais do próprio
 * usuário ({@code usuarioId = currentUser}).</p>
 *
 * <ul>
 *   <li>{@code GET   /api/notificacoes}                    — lista paginada</li>
 *   <li>{@code GET   /api/notificacoes/contagem}            — badge de não lidas</li>
 *   <li>{@code PATCH /api/notificacoes/{id}/lida}           — marca uma como lida</li>
 *   <li>{@code PATCH /api/notificacoes/marcar-todas-lidas}  — marca todas como lidas</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoController {

    private final NotificacaoService notificacaoService;
    private final TenantScopeGuard tenantScopeGuard;

    public NotificacaoController(NotificacaoService notificacaoService,
                                  TenantScopeGuard tenantScopeGuard) {
        this.notificacaoService = notificacaoService;
        this.tenantScopeGuard   = tenantScopeGuard;
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.READ)
    public Page<NotificacaoDto> listar(
            @RequestParam(required = false) Boolean apenasNaoLidas,
            Pageable pageable) {
        AuthContext ctx = tenantScopeGuard.currentContext();
        return notificacaoService.listar(ctx.tenantId(), ctx.userId(), apenasNaoLidas, pageable);
    }

    @GetMapping("/contagem")
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.READ)
    public Map<String, Long> contarNaoLidas() {
        AuthContext ctx = tenantScopeGuard.currentContext();
        return Map.of("total", notificacaoService.contarNaoLidas(ctx.tenantId(), ctx.userId()));
    }

    @PatchMapping("/{id}/lida")
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.UPDATE)
    public NotificacaoDto marcarComoLida(@PathVariable Long id) {
        AuthContext ctx = tenantScopeGuard.currentContext();
        return notificacaoService.marcarComoLida(ctx.tenantId(), ctx.userId(), id);
    }

    @PatchMapping("/marcar-todas-lidas")
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.UPDATE)
    public Map<String, Integer> marcarTodasComoLidas() {
        AuthContext ctx = tenantScopeGuard.currentContext();
        return Map.of("atualizadas", notificacaoService.marcarTodasComoLidas(ctx.tenantId(), ctx.userId()));
    }
}
