package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.*;
import com.erp.qualitascareapi.cme.application.QualidadeService;
import com.erp.qualitascareapi.quality.enums.NaoConformidadeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cme")
public class QualidadeController {

    private final QualidadeService qualidadeService;

    public QualidadeController(QualidadeService qualidadeService) {
        this.qualidadeService = qualidadeService;
    }

    @PostMapping("/exames-cultura")
    @ResponseStatus(HttpStatus.CREATED)
    public ExameCulturaDto registrarExame(@Validated @RequestBody ExameCulturaRequest request) {
        return qualidadeService.registrarExame(request);
    }

    @GetMapping("/exames-cultura")
    public Page<ExameCulturaDto> listExames(Pageable pageable) {
        return qualidadeService.listExames(pageable);
    }

    @PostMapping("/nao-conformidades")
    @ResponseStatus(HttpStatus.CREATED)
    public NaoConformidadeDto registrarNaoConformidade(@Validated @RequestBody NaoConformidadeRequest request) {
        return qualidadeService.registrarNaoConformidade(request);
    }

    @GetMapping("/nao-conformidades")
    public Page<NaoConformidadeDto> listNaoConformidades(Pageable pageable) {
        return qualidadeService.listNaoConformidades(pageable);
    }

    @GetMapping("/nao-conformidades/{id}")
    public NaoConformidadeDto getNaoConformidade(@PathVariable Long id) {
        return qualidadeService.findNaoConformidadeById(id);
    }

    @PatchMapping("/nao-conformidades/{id}/status")
    public NaoConformidadeDto updateNaoConformidadeStatus(@PathVariable Long id, @RequestParam NaoConformidadeStatus status) {
        return qualidadeService.updateNaoConformidadeStatus(id, status);
    }

    @PostMapping("/geracoes-residuo")
    @ResponseStatus(HttpStatus.CREATED)
    public GeracaoResiduoDto registrarGeracaoResiduo(@Validated @RequestBody GeracaoResiduoRequest request) {
        return qualidadeService.registrarGeracaoResiduo(request);
    }

    @GetMapping("/geracoes-residuo")
    public Page<GeracaoResiduoDto> listGeracoesResiduo(Pageable pageable) {
        return qualidadeService.listGeracoesResiduo(pageable);
    }
}
