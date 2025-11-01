package com.erp.qualitascareapi.iam.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.api.dto.RoleSummaryDto;
import com.erp.qualitascareapi.iam.api.dto.UserCreateRequest;
import com.erp.qualitascareapi.iam.api.dto.UserDto;
import com.erp.qualitascareapi.iam.api.dto.UserUpdateRequest;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.domains.Role;
import com.erp.qualitascareapi.security.enums.IdentityOrigin;
import com.erp.qualitascareapi.security.enums.UserStatus;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       TenantRepository tenantRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public Page<UserDto> list(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        return userRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional
    public UserDto create(UserCreateRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));

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
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        userRepository.deleteById(id);
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
}
