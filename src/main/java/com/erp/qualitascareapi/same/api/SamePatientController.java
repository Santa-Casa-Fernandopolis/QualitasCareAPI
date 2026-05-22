package com.erp.qualitascareapi.same.api;

import com.erp.qualitascareapi.same.api.dto.*;
import com.erp.qualitascareapi.same.application.SamePatientService;
import com.erp.qualitascareapi.same.matching.PatientMatchingService;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/same/patients")
@Validated
public class SamePatientController {

    private final SamePatientService patientService;
    private final PatientMatchingService matchingService;

    public SamePatientController(SamePatientService patientService,
                                 PatientMatchingService matchingService) {
        this.patientService = patientService;
        this.matchingService = matchingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.SAME_PATIENT, action = Action.CREATE)
    public SamePatientDto create(@Valid @RequestBody SamePatientRequest request) {
        return patientService.create(request);
    }

    @GetMapping("/{id}")
    @RequiresPermission(resource = ResourceType.SAME_PATIENT, action = Action.READ)
    public SamePatientDto findById(@PathVariable Long id) {
        return patientService.findById(id);
    }

    @GetMapping
    @RequiresPermission(resource = ResourceType.SAME_PATIENT, action = Action.READ)
    public Page<SamePatientDto> search(@RequestParam(required = false) String query,
                                       Pageable pageable) {
        return patientService.search(query, pageable);
    }

    @GetMapping("/search")
    @RequiresPermission(resource = ResourceType.SAME_PATIENT, action = Action.READ)
    public Page<SamePatientDto> searchAlias(@RequestParam(required = false) String query,
                                            Pageable pageable) {
        return patientService.search(query, pageable);
    }

    @PutMapping("/{id}")
    @RequiresPermission(resource = ResourceType.SAME_PATIENT, action = Action.UPDATE)
    public SamePatientDto update(@PathVariable Long id,
                                 @Valid @RequestBody SamePatientRequest request) {
        return patientService.update(id, request);
    }

    @PatchMapping("/{id}/status")
    @RequiresPermission(resource = ResourceType.SAME_PATIENT, action = Action.UPDATE)
    public SamePatientDto updateStatus(@PathVariable Long id,
                                       @Valid @RequestBody SamePatientStatusRequest request) {
        return patientService.updateStatus(id, request.status());
    }

    @GetMapping("/match-suggestions")
    @RequiresPermission(resource = ResourceType.SAME_PATIENT, action = Action.READ)
    public List<SamePatientMatchSuggestionDto> matchSuggestions(@RequestParam(required = false) String cpf,
                                                                @RequestParam(required = false) String cns,
                                                                @RequestParam(required = false) String fullName,
                                                                @RequestParam(required = false) String motherName,
                                                                @RequestParam(required = false) LocalDate birthDate) {
        return matchingService.suggest(cpf, cns, fullName, motherName, birthDate);
    }

    @PostMapping("/{patientId}/identifiers")
    @ResponseStatus(HttpStatus.CREATED)
    @RequiresPermission(resource = ResourceType.SAME_IDENTIFIER, action = Action.CREATE)
    public SamePatientIdentifierDto addIdentifier(@PathVariable Long patientId,
                                                  @Valid @RequestBody SamePatientIdentifierRequest request) {
        return patientService.addIdentifier(patientId, request);
    }

    @GetMapping("/{patientId}/identifiers")
    @RequiresPermission(resource = ResourceType.SAME_IDENTIFIER, action = Action.READ)
    public List<SamePatientIdentifierDto> listIdentifiers(@PathVariable Long patientId) {
        return patientService.listIdentifiers(patientId);
    }

    @DeleteMapping("/{patientId}/identifiers/{identifierId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequiresPermission(resource = ResourceType.SAME_IDENTIFIER, action = Action.DELETE)
    public void deleteIdentifier(@PathVariable Long patientId,
                                 @PathVariable Long identifierId) {
        patientService.deleteIdentifier(patientId, identifierId);
    }
}
