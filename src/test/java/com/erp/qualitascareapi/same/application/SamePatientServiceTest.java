package com.erp.qualitascareapi.same.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.same.api.dto.SamePatientIdentifierRequest;
import com.erp.qualitascareapi.same.api.dto.SamePatientRequest;
import com.erp.qualitascareapi.same.domain.SamePatientIdentifier;
import com.erp.qualitascareapi.same.domain.SamePatientMaster;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.same.repo.SamePatientIdentifierRepository;
import com.erp.qualitascareapi.same.repo.SamePatientMasterRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SamePatientServiceTest {

    @Mock
    private SamePatientMasterRepository patientRepository;

    @Mock
    private SamePatientIdentifierRepository identifierRepository;

    @Mock
    private TenantRepository tenantRepository;

    @Mock
    private TenantScopeGuard tenantScopeGuard;

    private SamePatientService service;

    @BeforeEach
    void setUp() {
        service = new SamePatientService(patientRepository, identifierRepository, tenantRepository, tenantScopeGuard);
    }

    @Test
    void create_normalizesCpfAndPersistsPatientInTenantScope() {
        Tenant tenant = new Tenant(1L, "1001", "Santa Casa", "12345678000100", null, true);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(patientRepository.save(any(SamePatientMaster.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(identifierRepository.findAllByTenantIdAndPatientMasterId(any(), any())).thenReturn(List.of());

        var dto = service.create(new SamePatientRequest(
                1L,
                "  Maria   Silva ",
                " Ana Silva ",
                LocalDate.of(1980, 1, 1),
                "123.456.789-01",
                null,
                null));

        assertThat(dto.fullName()).isEqualTo("Maria Silva");
        assertThat(dto.motherName()).isEqualTo("Ana Silva");
        assertThat(dto.cpf()).isEqualTo("12345678901");
        verify(tenantScopeGuard).checkRequestedTenant(1L);
    }

    @Test
    void addIdentifier_rejectsDuplicateSourceAndMedicalRecordCode() {
        SamePatientMaster patient = patient();
        when(tenantScopeGuard.currentTenantId()).thenReturn(1L);
        when(patientRepository.findByIdAndTenantId(10L, 1L)).thenReturn(Optional.of(patient));
        when(identifierRepository.existsByTenantIdAndSourceSystemAndMedicalRecordCode(
                1L, SameSourceSystem.WIRELINE, "WL-123")).thenReturn(true);

        assertThatThrownBy(() -> service.addIdentifier(10L, new SamePatientIdentifierRequest(
                SameSourceSystem.WIRELINE,
                "wl-123",
                null,
                false,
                null)))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Já existe um paciente vinculado");

        verify(identifierRepository, never()).save(any(SamePatientIdentifier.class));
    }

    private SamePatientMaster patient() {
        Tenant tenant = new Tenant(1L, "1001", "Santa Casa", "12345678000100", null, true);
        SamePatientMaster patient = new SamePatientMaster();
        patient.setId(10L);
        patient.setTenant(tenant);
        patient.setFullName("Maria Silva");
        return patient;
    }
}
