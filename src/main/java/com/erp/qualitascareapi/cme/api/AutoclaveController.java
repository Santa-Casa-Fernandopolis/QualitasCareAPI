package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.AutoclaveService;
import com.erp.qualitascareapi.cme.enums.CicloStatus;
import com.erp.qualitascareapi.cme.enums.ManutencaoStatus;
import com.erp.qualitascareapi.common.api.dto.EvidenciaArquivoDto;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/cme")
public class AutoclaveController {

    private final AutoclaveService autoclaveService;

    public AutoclaveController(AutoclaveService autoclaveService) {
        this.autoclaveService = autoclaveService;
    }

    @PostMapping("/autoclaves")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "CADASTRO")
    public AutoclaveDto createAutoclave(@Validated @RequestBody AutoclaveRequest request) {
        return autoclaveService.createAutoclave(request);
    }

    @GetMapping("/autoclaves")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<AutoclaveDto> listAutoclaves(Pageable pageable) {
        return autoclaveService.listAutoclaves(pageable);
    }

    @GetMapping("/autoclaves/{id}")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public AutoclaveDto getAutoclave(@PathVariable Long id) {
        return autoclaveService.findAutoclaveById(id);
    }

    @PutMapping("/autoclaves/{id}")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.UPDATE, feature = "CADASTRO")
    public AutoclaveDto updateAutoclave(@PathVariable Long id, @Validated @RequestBody AutoclaveRequest request) {
        return autoclaveService.updateAutoclave(id, request);
    }

    @PatchMapping("/autoclaves/{id}/status")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.UPDATE, feature = "CADASTRO")
    public AutoclaveDto updateAutoclaveStatus(@PathVariable Long id, @RequestParam Boolean ativo) {
        return autoclaveService.updateAutoclaveStatus(id, ativo);
    }

    @PostMapping("/autoclaves/planos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public PlanoPreventivoDto createPlanoPreventivo(@Validated @RequestBody PlanoPreventivoRequest request) {
        return autoclaveService.createPlanoPreventivo(request);
    }

    @GetMapping("/autoclaves/planos")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<PlanoPreventivoDto> listPlanos(Pageable pageable) {
        return autoclaveService.listPlanos(pageable);
    }

    @PostMapping("/autoclaves/manutencoes")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public ManutencaoDto registrarManutencao(@Validated @RequestBody ManutencaoRequest request) {
        return autoclaveService.registrarManutencao(request);
    }

    @GetMapping("/autoclaves/manutencoes")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<ManutencaoDto> listManutencoes(Pageable pageable) {
        return autoclaveService.listManutencoes(pageable);
    }

    @GetMapping("/autoclaves/manutencoes/{id}")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public ManutencaoDto getManutencao(@PathVariable Long id) {
        return autoclaveService.findManutencaoById(id);
    }

    @PatchMapping("/autoclaves/manutencoes/{id}/status")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.UPDATE, feature = "FORM")
    public ManutencaoDto updateManutencaoStatus(@PathVariable Long id, @RequestParam ManutencaoStatus status) {
        return autoclaveService.updateManutencaoStatus(id, status);
    }

    @PostMapping("/autoclaves/higienizacoes-profundas")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public HigienizacaoAutoclaveProfundaDto registrarHigienizacaoAutoclave(@Validated @RequestBody HigienizacaoAutoclaveProfundaRequest request) {
        return autoclaveService.registrarHigienizacaoAutoclave(request);
    }

    @GetMapping("/autoclaves/higienizacoes-profundas")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<HigienizacaoAutoclaveProfundaDto> listHigienizacoesAutoclave(Pageable pageable) {
        return autoclaveService.listHigienizacoesAutoclave(pageable);
    }

    @PostMapping("/higienizacoes-ultrassonica")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public HigienizacaoUltrassonicaDto registrarHigienizacaoUltrassonica(@Validated @RequestBody HigienizacaoUltrassonicaRequest request) {
        return autoclaveService.registrarHigienizacaoUltrassonica(request);
    }

    @GetMapping("/higienizacoes-ultrassonica")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<HigienizacaoUltrassonicaDto> listHigienizacoesUltrassonica(Pageable pageable) {
        return autoclaveService.listHigienizacoesUltrassonica(pageable);
    }

    @PostMapping("/testes/bowie-dick")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public TesteBowieDickDto registrarTesteBowieDick(@Validated @RequestBody TesteBowieDickRequest request) {
        return autoclaveService.registrarTesteBowieDick(request);
    }

    @PutMapping("/testes/bowie-dick/{id}")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.UPDATE, feature = "FORM")
    public TesteBowieDickDto atualizarTesteBowieDick(@PathVariable Long id,
                                                     @Validated @RequestBody TesteBowieDickRequest request) {
        return autoclaveService.atualizarTesteBowieDick(id, request);
    }

    @DeleteMapping("/testes/bowie-dick/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.DELETE, feature = "FORM")
    public void excluirTesteBowieDick(@PathVariable Long id) {
        autoclaveService.excluirTesteBowieDick(id);
    }

    @GetMapping("/testes/bowie-dick")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<TesteBowieDickDto> listTestesBowieDick(Pageable pageable) {
        return autoclaveService.listTestesBowieDick(pageable);
    }

    @PostMapping(value = "/testes/bowie-dick/evidencias", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public EvidenciaArquivoDto uploadTesteBowieDickImagem(@RequestPart("file") MultipartFile file) {
        return autoclaveService.uploadTesteBowieDickImagem(file);
    }

    @GetMapping("/testes/bowie-dick/evidencias/{evidenciaId}")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public ResponseEntity<Resource> viewTesteBowieDickImagem(@PathVariable Long evidenciaId) {
        EvidenciaArquivo evidencia = autoclaveService.findTesteBowieDickImagem(evidenciaId);
        Resource resource = autoclaveService.loadTesteBowieDickImagem(evidencia);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(evidencia.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline().filename(evidencia.getNomeArquivo()).build().toString())
                .body(resource);
    }

    @PatchMapping("/testes/bowie-dick/{id}/aprovar")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.UPDATE, feature = "FORM")
    public TesteBowieDickDto aprovarTesteBowieDick(@PathVariable Long id,
                                                   @RequestBody(required = false) BowieDickValidacaoRequest request) {
        return autoclaveService.validarTesteBowieDick(id, true, request);
    }

    @PatchMapping("/testes/bowie-dick/{id}/reprovar")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.UPDATE, feature = "FORM")
    public TesteBowieDickDto reprovarTesteBowieDick(@PathVariable Long id,
                                                    @RequestBody(required = false) BowieDickValidacaoRequest request) {
        return autoclaveService.validarTesteBowieDick(id, false, request);
    }

    @PostMapping("/ciclos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public CicloEsterilizacaoDto registrarCiclo(@Validated @RequestBody CicloEsterilizacaoRequest request) {
        return autoclaveService.registrarCiclo(request);
    }

    @GetMapping("/ciclos")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<CicloEsterilizacaoDto> listCiclos(Pageable pageable) {
        return autoclaveService.listCiclos(pageable);
    }

    @GetMapping("/ciclos/{id}")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public CicloEsterilizacaoDto getCiclo(@PathVariable Long id) {
        return autoclaveService.findCicloById(id);
    }

    @PatchMapping("/ciclos/{id}/status")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.UPDATE, feature = "FORM")
    public CicloEsterilizacaoDto updateCicloStatus(@PathVariable Long id, @RequestParam CicloStatus status) {
        return autoclaveService.updateCicloStatus(id, status);
    }

    @PostMapping("/indicadores/quimicos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public IndicadorQuimicoDto registrarIndicadorQuimico(@Validated @RequestBody IndicadorQuimicoRequest request) {
        return autoclaveService.registrarIndicadorQuimico(request);
    }

    @GetMapping("/indicadores/quimicos")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<IndicadorQuimicoDto> listIndicadoresQuimicos(Pageable pageable) {
        return autoclaveService.listIndicadoresQuimicos(pageable);
    }

    @PostMapping("/indicadores/biologicos")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.CREATE, feature = "FORM")
    public IndicadorBiologicoDto registrarIndicadorBiologico(@Validated @RequestBody IndicadorBiologicoRequest request) {
        return autoclaveService.registrarIndicadorBiologico(request);
    }

    @GetMapping("/indicadores/biologicos")
    @RequiresPermission(resource = ResourceType.CME_AUTOCLAVE, action = Action.READ, feature = "LISTA")
    public Page<IndicadorBiologicoDto> listIndicadoresBiologicos(Pageable pageable) {
        return autoclaveService.listIndicadoresBiologicos(pageable);
    }
}
