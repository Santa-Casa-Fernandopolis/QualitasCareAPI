package com.erp.qualitascareapi.notificacao.api;

import com.erp.qualitascareapi.notificacao.api.dto.NotificacaoDto;
import com.erp.qualitascareapi.notificacao.application.NotificacaoService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
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
 * <ul>
 *   <li>{@code GET  /api/notificacoes}                   — lista paginada (filtro ?apenasNaoLidas=true)</li>
 *   <li>{@code GET  /api/notificacoes/contagem}           — total de não lidas (badge)</li>
 *   <li>{@code PATCH /api/notificacoes/{id}/lida}         — marca uma notificação como lida</li>
 *   <li>{@code PATCH /api/notificacoes/marcar-todas-lidas}— marca todas como lidas</li>
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

    /**
     * Lista notificações do tenant autenticado, opcionalmente filtrando apenas as não lidas.
     *
     * @param apenasNaoLidas quando {@code true}, retorna somente notificações não lidas
     * @param pageable       paginação (padrão: page=0, size=20, sort=dataHora,desc)
     */
    @GetMapping
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.READ)
    public Page<NotificacaoDto> listar(
            @RequestParam(required = false) Boolean apenasNaoLidas,
            Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return notificacaoService.listar(tenantId, apenasNaoLidas, pageable);
    }

    /**
     * Retorna o total de notificações não lidas — usado para badge no frontend.
     *
     * @return {@code {"total": N}}
     */
    @GetMapping("/contagem")
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.READ)
    public Map<String, Long> contarNaoLidas() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return Map.of("total", notificacaoService.contarNaoLidas(tenantId));
    }

    /**
     * Marca uma notificação específica como lida.
     */
    @PatchMapping("/{id}/lida")
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.UPDATE)
    public NotificacaoDto marcarComoLida(@PathVariable Long id) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return notificacaoService.marcarComoLida(tenantId, id);
    }

    /**
     * Marca todas as notificações não lidas do tenant como lidas.
     *
     * @return {@code {"atualizadas": N}}
     */
    @PatchMapping("/marcar-todas-lidas")
    @RequiresPermission(resource = ResourceType.NOTIFICACAO, action = Action.UPDATE)
    public Map<String, Integer> marcarTodasComoLidas() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        return Map.of("atualizadas", notificacaoService.marcarTodasComoLidas(tenantId));
    }
}
