package com.erp.qualitascareapi.same.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.same.domain.SameClinicalDocument;
import com.erp.qualitascareapi.same.domain.SamePatientMaster;
import com.erp.qualitascareapi.same.enums.SameAccessAction;
import com.erp.qualitascareapi.same.enums.SameDocumentType;
import com.erp.qualitascareapi.same.enums.SameSourceSystem;
import com.erp.qualitascareapi.same.repo.SameClinicalDocumentRepository;
import com.erp.qualitascareapi.same.repo.SamePatientIdentifierRepository;
import com.erp.qualitascareapi.same.repo.SamePatientMasterRepository;
import com.erp.qualitascareapi.same.storage.SameFileStorageService;
import com.erp.qualitascareapi.same.storage.SameStoredFile;
import com.erp.qualitascareapi.security.app.AuthContext;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SameClinicalDocumentServiceTest {

    @Mock
    private SameClinicalDocumentRepository documentRepository;

    @Mock
    private SamePatientMasterRepository patientRepository;

    @Mock
    private SamePatientIdentifierRepository identifierRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SameFileStorageService fileStorageService;

    @Mock
    private SameDocumentAuditService auditService;

    @Mock
    private TenantScopeGuard tenantScopeGuard;

    private SameClinicalDocumentService service;

    @BeforeEach
    void setUp() {
        service = new SameClinicalDocumentService(
                documentRepository,
                patientRepository,
                identifierRepository,
                userRepository,
                fileStorageService,
                auditService,
                tenantScopeGuard,
                1024 * 1024);
    }

    @Test
    void upload_rejectsNonPdfFile() {
        SamePatientMaster patient = patient();
        when(tenantScopeGuard.currentTenantId()).thenReturn(1L);
        when(patientRepository.findByIdAndTenantId(10L, 1L)).thenReturn(Optional.of(patient));

        MockMultipartFile file = new MockMultipartFile(
                "file", "prontuario.txt", "text/plain", "conteudo".getBytes());

        assertThatThrownBy(() -> service.upload(
                10L,
                null,
                SameDocumentType.PRONTUARIO_DIGITALIZADO,
                SameSourceSystem.WIRELINE,
                "WL-123",
                null,
                null,
                null,
                null,
                null,
                file,
                new MockHttpServletRequest()))
                .isInstanceOf(ApplicationException.class)
                .hasMessageContaining("Envie apenas arquivos PDF");

        verify(fileStorageService, never()).storePdf(any(), any(), any());
        verify(documentRepository, never()).save(any());
    }

    @Test
    void upload_storesPdfAndRegistersAuditLog() {
        SamePatientMaster patient = patient();
        when(tenantScopeGuard.currentTenantId()).thenReturn(1L);
        when(tenantScopeGuard.currentContext()).thenReturn(new AuthContext(
                null, "same.oper.scf", 1L, Set.of("SAME_OPERATOR"), null, null, null, null, null));
        when(patientRepository.findByIdAndTenantId(10L, 1L)).thenReturn(Optional.of(patient));
        when(fileStorageService.storePdf(any(), eq(1L), eq(10L))).thenReturn(new SameStoredFile(
                "same-doc.pdf",
                "tenant-1/patient-10/same-doc.pdf",
                "abc123",
                "application/pdf",
                100L));
        when(documentRepository.save(any(SameClinicalDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MockMultipartFile file = new MockMultipartFile(
                "file", "prontuario.pdf", "application/pdf", "%PDF".getBytes());

        var dto = service.upload(
                10L,
                null,
                SameDocumentType.PRONTUARIO_DIGITALIZADO,
                SameSourceSystem.WIRELINE,
                "wl-123",
                null,
                null,
                null,
                null,
                "Prontuário antigo",
                file,
                new MockHttpServletRequest());

        assertThat(dto.fileName()).isEqualTo("prontuario.pdf");
        assertThat(dto.fileHashSha256()).isEqualTo("abc123");
        assertThat(dto.originalMedicalRecordCode()).isEqualTo("WL-123");
        verify(auditService).register(any(SameClinicalDocument.class), eq(SameAccessAction.UPLOAD), any());
    }

    private SamePatientMaster patient() {
        Tenant tenant = new Tenant(1L, "1001", "Santa Casa", "12345678000100", null, true);
        SamePatientMaster patient = new SamePatientMaster();
        patient.setId(10L);
        patient.setTenant(tenant);
        patient.setFullName("Maria Silva");
        patient.setCpf("12345678901");
        return patient;
    }
}
