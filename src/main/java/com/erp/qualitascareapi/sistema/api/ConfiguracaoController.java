package com.erp.qualitascareapi.sistema.api;

import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import com.erp.qualitascareapi.sistema.api.dto.ConfiguracaoDto;
import com.erp.qualitascareapi.sistema.api.dto.ConfiguracaoRequest;
import com.erp.qualitascareapi.sistema.api.dto.ConfiguracaoUpdateRequest;
import com.erp.qualitascareapi.sistema.application.ConfiguracaoService;
import com.erp.qualitascareapi.sistema.enums.ModuloConfiguracao;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API de gerenciamento de parâmetros de sistema.
 *
 * <h3>Endpoints</h3>
 * <pre>
 * GET    /api/admin/configuracoes?modulo=MV           — lista por módulo (global)
 * GET    /api/admin/configuracoes/{id}                — detalhe de um parâmetro
 * POST   /api/admin/configuracoes                     — cria novo parâmetro
 * PUT    /api/admin/configuracoes/{id}/valor          — atualiza valor
 * DELETE /api/admin/configuracoes/{id}                — remove (somente editavel=true)
 * </pre>
 *
 * <p>Valores do tipo {@code SECRET} são retornados mascarados ({@code "****"}) nas respostas.</p>
 */
@RestController
@RequestMapping("/api/admin/configuracoes")
public class ConfiguracaoController {

    private final ConfiguracaoService configuracaoService;

    public ConfiguracaoController(ConfiguracaoService configuracaoService) {
        this.configuracaoService = configuracaoService;
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.SYS_CONFIGURACAO, action = Action.READ)
    public List<ConfiguracaoDto> listar(@RequestParam ModuloConfiguracao modulo,
                                        @RequestParam(required = false) Long tenantId) {
        return tenantId != null
                ? configuracaoService.listarPorModuloETenant(modulo, tenantId)
                : configuracaoService.listarPorModulo(modulo);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.SYS_CONFIGURACAO, action = Action.READ)
    public ConfiguracaoDto findById(@PathVariable Long id) {
        return configuracaoService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.SYS_CONFIGURACAO, action = Action.CREATE)
    public ConfiguracaoDto criar(@Validated @RequestBody ConfiguracaoRequest request) {
        return configuracaoService.criar(request);
    }

    @PutMapping("/{id}/valor")
    @RequiresPermission(resource = ResourceType.SYS_CONFIGURACAO, action = Action.UPDATE)
    public ConfiguracaoDto atualizar(@PathVariable Long id,
                                     @Validated @RequestBody ConfiguracaoUpdateRequest request) {
        return configuracaoService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.SYS_CONFIGURACAO, action = Action.DELETE)
    public void deletar(@PathVariable Long id) {
        configuracaoService.deletar(id);
    }
}
