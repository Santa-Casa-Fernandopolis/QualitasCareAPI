package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.AutoclaveService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cme")
public class AutoclaveController {

    private final AutoclaveService autoclaveService;

    public AutoclaveController(AutoclaveService autoclaveService) {
        this.autoclaveService = autoclaveService;
    }

    @PostMapping("/autoclaves")
    @ResponseStatus(HttpStatus.CREATED)
    public AutoclaveDto createAutoclave(@Validated @RequestBody AutoclaveRequest request) {
        return autoclaveService.createAutoclave(request);
    }

    @GetMapping("/autoclaves")
    public Page<AutoclaveDto> listAutoclaves(Pageable pageable) {
        return autoclaveService.listAutoclaves(pageable);
    }

    @PostMapping("/autoclaves/planos")
    @ResponseStatus(HttpStatus.CREATED)
    public PlanoPreventivoDto createPlanoPreventivo(@Validated @RequestBody PlanoPreventivoRequest request) {
        return autoclaveService.createPlanoPreventivo(request);
    }

    @GetMapping("/autoclaves/planos")
    public Page<PlanoPreventivoDto> listPlanos(Pageable pageable) {
        return autoclaveService.listPlanos(pageable);
    }

    @PostMapping("/autoclaves/manutencoes")
    @ResponseStatus(HttpStatus.CREATED)
    public ManutencaoDto registrarManutencao(@Validated @RequestBody ManutencaoRequest request) {
        return autoclaveService.registrarManutencao(request);
    }

    @GetMapping("/autoclaves/manutencoes")
    public Page<ManutencaoDto> listManutencoes(Pageable pageable) {
        return autoclaveService.listManutencoes(pageable);
    }

    @PostMapping("/autoclaves/higienizacoes-profundas")
    @ResponseStatus(HttpStatus.CREATED)
    public HigienizacaoAutoclaveProfundaDto registrarHigienizacaoAutoclave(@Validated @RequestBody HigienizacaoAutoclaveProfundaRequest request) {
        return autoclaveService.registrarHigienizacaoAutoclave(request);
    }

    @GetMapping("/autoclaves/higienizacoes-profundas")
    public Page<HigienizacaoAutoclaveProfundaDto> listHigienizacoesAutoclave(Pageable pageable) {
        return autoclaveService.listHigienizacoesAutoclave(pageable);
    }

    @PostMapping("/higienizacoes-ultrassonica")
    @ResponseStatus(HttpStatus.CREATED)
    public HigienizacaoUltrassonicaDto registrarHigienizacaoUltrassonica(@Validated @RequestBody HigienizacaoUltrassonicaRequest request) {
        return autoclaveService.registrarHigienizacaoUltrassonica(request);
    }

    @GetMapping("/higienizacoes-ultrassonica")
    public Page<HigienizacaoUltrassonicaDto> listHigienizacoesUltrassonica(Pageable pageable) {
        return autoclaveService.listHigienizacoesUltrassonica(pageable);
    }

    @PostMapping("/testes/bowie-dick")
    @ResponseStatus(HttpStatus.CREATED)
    public TesteBowieDickDto registrarTesteBowieDick(@Validated @RequestBody TesteBowieDickRequest request) {
        return autoclaveService.registrarTesteBowieDick(request);
    }

    @GetMapping("/testes/bowie-dick")
    public Page<TesteBowieDickDto> listTestesBowieDick(Pageable pageable) {
        return autoclaveService.listTestesBowieDick(pageable);
    }

    @PostMapping("/ciclos")
    @ResponseStatus(HttpStatus.CREATED)
    public CicloEsterilizacaoDto registrarCiclo(@Validated @RequestBody CicloEsterilizacaoRequest request) {
        return autoclaveService.registrarCiclo(request);
    }

    @GetMapping("/ciclos")
    public Page<CicloEsterilizacaoDto> listCiclos(Pageable pageable) {
        return autoclaveService.listCiclos(pageable);
    }

    @PostMapping("/indicadores/quimicos")
    @ResponseStatus(HttpStatus.CREATED)
    public IndicadorQuimicoDto registrarIndicadorQuimico(@Validated @RequestBody IndicadorQuimicoRequest request) {
        return autoclaveService.registrarIndicadorQuimico(request);
    }

    @GetMapping("/indicadores/quimicos")
    public Page<IndicadorQuimicoDto> listIndicadoresQuimicos(Pageable pageable) {
        return autoclaveService.listIndicadoresQuimicos(pageable);
    }

    @PostMapping("/indicadores/biologicos")
    @ResponseStatus(HttpStatus.CREATED)
    public IndicadorBiologicoDto registrarIndicadorBiologico(@Validated @RequestBody IndicadorBiologicoRequest request) {
        return autoclaveService.registrarIndicadorBiologico(request);
    }

    @GetMapping("/indicadores/biologicos")
    public Page<IndicadorBiologicoDto> listIndicadoresBiologicos(Pageable pageable) {
        return autoclaveService.listIndicadoresBiologicos(pageable);
    }
}
