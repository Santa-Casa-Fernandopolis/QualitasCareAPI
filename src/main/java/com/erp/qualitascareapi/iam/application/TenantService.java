package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.common.application.EvidenciaArquivoStorageService;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.api.dto.TenantDto;
import com.erp.qualitascareapi.iam.api.dto.TenantLoginOptionDto;
import com.erp.qualitascareapi.iam.api.dto.TenantRequest;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final EvidenciaArquivoStorageService evidenciaArquivoStorageService;
    private final TenantScopeGuard tenantScopeGuard;

    public TenantService(TenantRepository tenantRepository,
                         UserRepository userRepository,
                         EvidenciaArquivoRepository evidenciaArquivoRepository,
                         EvidenciaArquivoStorageService evidenciaArquivoStorageService,
                         TenantScopeGuard tenantScopeGuard) {
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.evidenciaArquivoStorageService = evidenciaArquivoStorageService;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<TenantDto> list(Pageable pageable) {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (tenantId != null) {
            return tenantRepository.findById(tenantId)
                    .map(this::toDto)
                    .map(dto -> new PageImpl<>(List.of(dto), pageable, 1))
                    .orElseGet(() -> new PageImpl<>(List.of(), pageable, 0));
        }
        return tenantRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public TenantDto get(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
        tenantScopeGuard.checkTenantAccess(tenant.getId());
        return toDto(tenant);
    }

    @Transactional
    public TenantDto create(TenantRequest request) {
        Tenant tenant = new Tenant();
        applyRequest(request, tenant);
        return toDto(tenantRepository.save(tenant));
    }

    @Transactional
    public TenantDto update(Long id, TenantRequest request) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
        tenantScopeGuard.checkTenantAccess(tenant.getId());
        applyRequest(request, tenant);
        return toDto(tenant);
    }

    @Transactional
    public void delete(Long id) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
        tenantScopeGuard.checkTenantAccess(tenant.getId());
        tenantRepository.delete(tenant);
    }

    @Transactional
    public TenantDto uploadLogo(Long id, MultipartFile file) {
        Tenant tenant = tenantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", id));
        tenantScopeGuard.checkTenantAccess(tenant.getId());

        EvidenciaArquivo evidencia = evidenciaArquivoStorageService.storeImage(tenant, file, currentUser(), "logos");
        tenant.setLogo("/api/tenants/logos/" + evidencia.getId());
        return toDto(tenant);
    }

    @Transactional(readOnly = true)
    public EvidenciaArquivo findLogo(Long evidenciaId) {
        return evidenciaArquivoRepository.findById(evidenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Logo", evidenciaId));
    }

    public Resource loadLogo(EvidenciaArquivo evidencia) {
        return evidenciaArquivoStorageService.loadAsResource(evidencia);
    }

    private void applyRequest(TenantRequest request, Tenant tenant) {
        tenant.setCode(request.code());
        tenant.setName(request.name());
        tenant.setCnpj(request.cnpj());
        tenant.setLogo(request.logo());
        tenant.setActive(request.active());
    }

    private TenantDto toDto(Tenant tenant) {
        return new TenantDto(
                tenant.getId(),
                tenant.getCode(),
                tenant.getName(),
                tenant.getCnpj(),
                tenant.getLogo(),
                tenant.isActive());
    }

    @Transactional(readOnly = true)
    public List<TenantLoginOptionDto> findAvailableTenantsForUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return List.of();
        }

        String normalized = username.trim();

        List<User> users = userRepository.findAllByUsernameIgnoreCase(normalized);

        Map<Long, User> usersByTenantId = users.stream()
                .filter(user -> user.getTenant() != null)
                .filter(user -> user.getTenant().isActive())
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        user -> user.getTenant().getId(),
                        Function.identity(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));

        return usersByTenantId.values().stream()
                .sorted(Comparator.comparing(
                        user -> user.getTenant().getName(),
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(user -> toLoginDto(user.getTenant(), user.getPhotoUrl()))
                .collect(Collectors.toList());
    }

    private TenantLoginOptionDto toLoginDto(Tenant tenant, String userPhotoUrl) {
        return new TenantLoginOptionDto(
                tenant.getId(),
                tenant.getCode(),
                tenant.getName(),
                tenant.getCnpj(),
                tenant.getLogo(),
                userPhotoUrl);
    }

    private User currentUser() {
        Long userId = tenantScopeGuard.currentContext().userId();
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }
}
