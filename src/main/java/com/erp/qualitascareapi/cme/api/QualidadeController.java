package com.erp.qualitascareapi.cme.api;

import com.erp.qualitascareapi.cme.api.dto.ExameCulturaDto;
import com.erp.qualitascareapi.cme.api.dto.ExameCulturaRequest;
import com.erp.qualitascareapi.cme.application.QualidadeService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
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
    @RequiresPermission(resource = ResourceType.CME_QUALIDADE, action = Action.CREATE)
    public ExameCulturaDto registrarExame(@Validated @RequestBody ExameCulturaRequest request) {
        return qualidadeService.registrarExame(request);
    }

    @GetMapping("/exames-cultura")
    @RequiresPermission(resource = ResourceType.CME_QUALIDADE, action = Action.READ)
    public Page<ExameCulturaDto> listExames(Pageable pageable) {
        return qualidadeService.listExames(pageable);
    }
}
