package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.common.application.EvidenciaArquivoStorageService;
import com.erp.qualitascareapi.common.domain.EvidenciaArquivo;
import com.erp.qualitascareapi.common.repo.EvidenciaArquivoRepository;
import com.erp.qualitascareapi.iam.api.dto.RoleSummaryDto;
import com.erp.qualitascareapi.iam.api.dto.UserCreateRequest;
import com.erp.qualitascareapi.iam.api.dto.UserDto;
import com.erp.qualitascareapi.iam.api.dto.UserProfileUpdateRequest;
import com.erp.qualitascareapi.iam.api.dto.UserUpdateRequest;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import com.erp.qualitascareapi.security.domain.Role;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final EvidenciaArquivoRepository evidenciaArquivoRepository;
    private final EvidenciaArquivoStorageService evidenciaArquivoStorageService;
    private final PasswordEncoder passwordEncoder;
    private final TenantScopeGuard tenantScopeGuard;

    public UserService(UserRepository userRepository,
                       TenantRepository tenantRepository,
                       RoleRepository roleRepository,
                       EvidenciaArquivoRepository evidenciaArquivoRepository,
                       EvidenciaArquivoStorageService evidenciaArquivoStorageService,
                       PasswordEncoder passwordEncoder,
                       TenantScopeGuard tenantScopeGuard) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
        this.evidenciaArquivoRepository = evidenciaArquivoRepository;
        this.evidenciaArquivoStorageService = evidenciaArquivoStorageService;
        this.passwordEncoder = passwordEncoder;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<UserDto> list(String username,
                              String fullName,
                              UserStatus status,
                              IdentityOrigin origin,
                              Long tenantId,
                              Pageable pageable) {
        Long contextTenantId = tenantScopeGuard.currentTenantId();
        Long effectiveTenantId = contextTenantId != null ? contextTenantId : tenantId;
        if (contextTenantId != null) {
            tenantScopeGuard.checkTenantAccess(effectiveTenantId);
        }
        return userRepository.findAll(buildSpecification(
                        effectiveTenantId,
                        emptyToNull(username),
                        emptyToNull(fullName),
                        status,
                        origin
                ), pageable)
                .map(this::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        tenantScopeGuard.checkTenantAccess(user.getTenant() != null ? user.getTenant().getId() : null);
        return toDto(user);
    }

    @Transactional
    public UserDto create(UserCreateRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        tenantScopeGuard.checkTenantAccess(tenant.getId());

        User user = new User();
        user.setTenant(tenant);
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user.setDepartment(request.department());
        user.setActivatedAt(request.activatedAt());
        user.setExpiresAt(request.expiresAt());
        user.setStatus(request.status() != null ? request.status() : UserStatus.PROVISIONED);
        user.setOrigin(request.origin() != null ? request.origin() : IdentityOrigin.LOCAL);

        applyRoles(user, tenant.getId(), request.roleIds());

        return toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        tenantScopeGuard.checkTenantAccess(user.getTenant() != null ? user.getTenant().getId() : null);

        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.department() != null) {
            user.setDepartment(request.department());
        }
        if (request.status() != null) {
            user.setStatus(request.status());
        }
        if (request.origin() != null) {
            user.setOrigin(request.origin());
        }
        if (request.activatedAt() != null || user.getActivatedAt() != null) {
            user.setActivatedAt(request.activatedAt());
        }
        if (request.expiresAt() != null || user.getExpiresAt() != null) {
            user.setExpiresAt(request.expiresAt());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        applyRoles(user, user.getTenant().getId(), request.roleIds());

        return toDto(user);
    }

    @Transactional
    public UserDto updateProfile(Long id, UserProfileUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        tenantScopeGuard.checkTenantAccess(user.getTenant() != null ? user.getTenant().getId() : null);

        if (request.fullName() != null) {
            user.setFullName(request.fullName());
        }
        if (request.department() != null) {
            user.setDepartment(request.department());
        }
        if (request.password() != null && !request.password().isBlank()) {
            user.setPasswordHash(passwordEncoder.encode(request.password()));
        }

        return toDto(user);
    }

    @Transactional
    public UserDto uploadPhoto(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        tenantScopeGuard.checkTenantAccess(user.getTenant() != null ? user.getTenant().getId() : null);

        EvidenciaArquivo evidencia = evidenciaArquivoStorageService.storeImage(user.getTenant(), file, currentUser(), "usuarios");
        user.setPhotoUrl("/api/users/photos/" + evidencia.getId());
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public EvidenciaArquivo findPhoto(Long evidenciaId) {
        return evidenciaArquivoRepository.findById(evidenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Foto do usuário", evidenciaId));
    }

    public Resource loadPhoto(EvidenciaArquivo evidencia) {
        return evidenciaArquivoStorageService.loadAsResource(evidencia);
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        tenantScopeGuard.checkTenantAccess(user.getTenant() != null ? user.getTenant().getId() : null);
        userRepository.delete(user);
    }

    private void applyRoles(User user, Long tenantId, Set<Long> roleIds) {
        if (roleIds == null) {
            return;
        }
        Set<Role> roles = new HashSet<>(roleRepository.findAllById(roleIds));
        if (roles.size() != roleIds.size()) {
            throw new BadRequestException("Some roles were not found", Map.of("roleIds", roleIds));
        }
        boolean invalidTenant = roles.stream().anyMatch(role -> !role.getTenant().getId().equals(tenantId));
        if (invalidTenant) {
            throw new BadRequestException("Role tenant mismatch", Map.of("tenantId", tenantId));
        }
        user.setRoles(roles);
    }

    private UserDto toDto(User user) {
        Set<RoleSummaryDto> roles = user.getRoles().stream()
                .map(role -> new RoleSummaryDto(role.getId(), role.getName(), role.getDescription()))
                .collect(Collectors.toSet());
        Tenant tenant = user.getTenant();
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getDepartment(),
                user.getPhotoUrl(),
                user.getStatus(),
                user.getOrigin(),
                user.getCreatedAt(),
                user.getActivatedAt(),
                user.getExpiresAt(),
                user.getUpdatedAt(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getCode() : null,
                roles
        );
    }

    private User currentUser() {
        Long userId = tenantScopeGuard.currentContext().userId();
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId).orElse(null);
    }

    private String emptyToNull(String value) {
        return (value == null || value.isBlank()) ? null : value;
    }

    private Specification<User> buildSpecification(Long tenantId,
                                                    String username,
                                                    String fullName,
                                                    UserStatus status,
                                                    IdentityOrigin origin) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.conjunction();

            if (tenantId != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("tenant").get("id"), tenantId));
            }
            if (username != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("username")),
                                "%" + username.toLowerCase() + "%"
                        )
                );
            }
            if (fullName != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("fullName")),
                                "%" + fullName.toLowerCase() + "%"
                        )
                );
            }
            if (status != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("status"), status));
            }
            if (origin != null) {
                predicate = criteriaBuilder.and(predicate, criteriaBuilder.equal(root.get("origin"), origin));
            }

            return predicate;
        };
    }
}
