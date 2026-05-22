package com.erp.qualitascareapi.same.api;

import com.erp.qualitascareapi.same.api.dto.SameLegacyPatientSnapshotDto;
import com.erp.qualitascareapi.same.application.SameLegacyPatientService;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.security.annotation.RequiresPermission;
import com.erp.qualitascareapi.security.enums.Action;
import com.erp.qualitascareapi.security.enums.ResourceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/same/legacy-patients")
public class SameLegacyPatientController {

    private final SameLegacyPatientService service;

    public SameLegacyPatientController(SameLegacyPatientService service) {
        this.service = service;
    }

    @GetMapping("/search/by-cpf")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.READ)
    public List<SameLegacyPatientSnapshotDto> searchByCpf(@RequestParam SameSourceSystem sourceSystem,
                                                          @RequestParam String cpf) {
        return service.searchByCpf(sourceSystem, cpf);
    }

    @GetMapping("/search/by-record")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.READ)
    public List<SameLegacyPatientSnapshotDto> searchByMedicalRecordCode(@RequestParam SameSourceSystem sourceSystem,
                                                                        @RequestParam String medicalRecordCode) {
        return service.searchByMedicalRecordCode(sourceSystem, medicalRecordCode);
    }

    @GetMapping("/search/by-name-birth-date")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.READ)
    public List<SameLegacyPatientSnapshotDto> searchByNameAndBirthDate(
            @RequestParam SameSourceSystem sourceSystem,
            @RequestParam String fullName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate) {
        return service.searchByNameAndBirthDate(sourceSystem, fullName, birthDate);
    }

    @GetMapping("/search/by-external-id")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.READ)
    public SameLegacyPatientSnapshotDto getByExternalPatientId(@RequestParam SameSourceSystem sourceSystem,
                                                              @RequestParam String externalPatientId) {
        return service.getByExternalPatientId(sourceSystem, externalPatientId);
    }

    @GetMapping("/snapshots")
    @RequiresPermission(resource = ResourceType.SAME_LEGACY_SOURCE, action = Action.READ)
    public Page<SameLegacyPatientSnapshotDto> listSnapshots(@RequestParam(required = false) SameSourceSystem sourceSystem,
                                                           @RequestParam(required = false) String cpf,
                                                           @RequestParam(required = false) String medicalRecordCode,
                                                           Pageable pageable) {
        return service.listSnapshots(sourceSystem, cpf, medicalRecordCode, pageable);
    }
}
