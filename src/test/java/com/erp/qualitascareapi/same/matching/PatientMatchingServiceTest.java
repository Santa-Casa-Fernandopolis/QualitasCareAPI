package com.erp.qualitascareapi.same.matching;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.same.domain.SamePatientMaster;
import com.erp.qualitascareapi.same.enums.SameConfidenceLevel;
import com.erp.qualitascareapi.same.repo.SamePatientMasterRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatientMatchingServiceTest {

    @Mock
    private SamePatientMasterRepository patientRepository;

    @Mock
    private TenantScopeGuard tenantScopeGuard;

    private PatientMatchingService service;

    @BeforeEach
    void setUp() {
        service = new PatientMatchingService(patientRepository, tenantScopeGuard);
    }

    @Test
    void suggest_returnsHighConfidenceWhenCpfMatches() {
        SamePatientMaster patient = patient(10L, "Maria Silva", "Ana Silva", "12345678901");

        when(tenantScopeGuard.currentTenantId()).thenReturn(1L);
        when(patientRepository.findAllByTenantIdAndCpf(eq(1L), eq("12345678901"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(patient)));
        when(patientRepository.findAllByTenantIdAndBirthDate(anyLong(), any(), any(Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty());

        var suggestions = service.suggest("123.456.789-01", null, null, null, LocalDate.of(1980, 1, 1));

        assertThat(suggestions).hasSize(1);
        assertThat(suggestions.getFirst().patientMasterId()).isEqualTo(10L);
        assertThat(suggestions.getFirst().confidenceLevel()).isEqualTo(SameConfidenceLevel.HIGH);
        assertThat(suggestions.getFirst().reason()).isEqualTo("CPF igual");
    }

    @Test
    void suggest_returnsMediumConfidenceForNameAndBirthDate() {
        SamePatientMaster patient = patient(11L, "Jose Pereira", "Maria Pereira", null);
        LocalDate birthDate = LocalDate.of(1975, 5, 20);
        patient.setBirthDate(birthDate);

        when(tenantScopeGuard.currentTenantId()).thenReturn(1L);
        when(patientRepository.findAllByTenantIdAndBirthDate(eq(1L), eq(birthDate), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(patient)));

        var suggestions = service.suggest(null, null, "José Pereira", null, birthDate);

        assertThat(suggestions).hasSize(1);
        assertThat(suggestions.getFirst().confidenceLevel()).isEqualTo(SameConfidenceLevel.MEDIUM);
        assertThat(suggestions.getFirst().reason()).isEqualTo("Nome e nascimento iguais");
    }

    private SamePatientMaster patient(Long id, String fullName, String motherName, String cpf) {
        SamePatientMaster patient = new SamePatientMaster();
        patient.setId(id);
        patient.setTenant(new Tenant(1L));
        patient.setFullName(fullName);
        patient.setMotherName(motherName);
        patient.setCpf(cpf);
        return patient;
    }
}
