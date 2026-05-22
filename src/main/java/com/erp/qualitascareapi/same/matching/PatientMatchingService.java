package com.erp.qualitascareapi.same.matching;

import com.erp.qualitascareapi.same.api.dto.SamePatientMatchSuggestionDto;
import com.erp.qualitascareapi.same.domain.SamePatientMaster;
import com.erp.qualitascareapi.same.enums.SameConfidenceLevel;
import com.erp.qualitascareapi.same.repo.SamePatientMasterRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class PatientMatchingService {

    private static final int MAX_SUGGESTIONS = 20;

    private final SamePatientMasterRepository patientRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public PatientMatchingService(SamePatientMasterRepository patientRepository,
                                  TenantScopeGuard tenantScopeGuard) {
        this.patientRepository = patientRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    public List<SamePatientMatchSuggestionDto> suggest(String cpf,
                                                       String cns,
                                                       String fullName,
                                                       String motherName,
                                                       LocalDate birthDate) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        Map<Long, SamePatientMatchSuggestionDto> suggestions = new LinkedHashMap<>();

        if (hasText(cpf)) {
            patientRepository.findAllByTenantIdAndCpf(tenantId, onlyDigits(cpf), PageRequest.of(0, MAX_SUGGESTIONS))
                    .forEach(patient -> add(suggestions, patient, SameConfidenceLevel.HIGH, "CPF igual"));
        }

        if (hasText(cns)) {
            patientRepository.findAllByTenantIdAndCns(tenantId, onlyDigits(cns), PageRequest.of(0, MAX_SUGGESTIONS))
                    .forEach(patient -> add(suggestions, patient, SameConfidenceLevel.HIGH, "CNS igual"));
        }

        if (birthDate != null && suggestions.size() < MAX_SUGGESTIONS) {
            patientRepository.findAllByTenantIdAndBirthDate(tenantId, birthDate, PageRequest.of(0, MAX_SUGGESTIONS))
                    .forEach(patient -> evaluateDemographicMatch(suggestions, patient, fullName, motherName));
        }

        return suggestions.values().stream()
                .limit(MAX_SUGGESTIONS)
                .toList();
    }

    private void evaluateDemographicMatch(Map<Long, SamePatientMatchSuggestionDto> suggestions,
                                          SamePatientMaster patient,
                                          String fullName,
                                          String motherName) {
        String storedName = normalize(patient.getFullName());
        String candidateName = normalize(fullName);
        String storedMother = normalize(patient.getMotherName());
        String candidateMother = normalize(motherName);

        if (hasText(candidateName) && storedName.equals(candidateName)
                && hasText(candidateMother) && storedMother.equals(candidateMother)) {
            add(suggestions, patient, SameConfidenceLevel.HIGH, "Nome, nascimento e mãe iguais");
            return;
        }

        if (hasText(candidateName) && storedName.equals(candidateName)) {
            add(suggestions, patient, SameConfidenceLevel.MEDIUM, "Nome e nascimento iguais");
            return;
        }

        if (hasText(candidateName) && isSimilar(storedName, candidateName)) {
            add(suggestions, patient, SameConfidenceLevel.LOW, "Nome semelhante e nascimento igual");
        }
    }

    private void add(Map<Long, SamePatientMatchSuggestionDto> suggestions,
                     SamePatientMaster patient,
                     SameConfidenceLevel level,
                     String reason) {
        suggestions.putIfAbsent(patient.getId(), new SamePatientMatchSuggestionDto(
                patient.getId(),
                patient.getFullName(),
                patient.getMotherName(),
                patient.getBirthDate(),
                patient.getCpf(),
                patient.getCns(),
                patient.getStatus(),
                level,
                reason
        ));
    }

    private boolean isSimilar(String a, String b) {
        if (!hasText(a) || !hasText(b)) {
            return false;
        }
        return a.contains(b) || b.contains(a) || levenshtein(a, b) <= 3;
    }

    private int levenshtein(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) {
            costs[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int corner = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int upper = costs[j];
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                costs[j] = Math.min(Math.min(costs[j - 1] + 1, costs[j] + 1), corner + cost);
                corner = upper;
            }
        }
        return costs[b.length()];
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.trim().toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    private String onlyDigits(String value) {
        return value == null ? null : value.replaceAll("\\D", "");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
