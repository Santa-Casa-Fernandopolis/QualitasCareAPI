package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import com.erp.qualitascareapi.security.api.dto.UserPermissionOverrideDto;
import com.erp.qualitascareapi.security.api.dto.UserPermissionOverrideRequest;
import com.erp.qualitascareapi.security.domains.UserPermissionOverride;
import com.erp.qualitascareapi.security.repo.UserPermissionOverrideRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class UserPermissionOverrideService {

    private final UserPermissionOverrideRepository overrideRepository;
    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;

    public UserPermissionOverrideService(UserPermissionOverrideRepository overrideRepository,
                                         UserRepository userRepository,
                                         TenantRepository tenantRepository) {
        this.overrideRepository = overrideRepository;
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional(readOnly = true)
    public Page<UserPermissionOverrideDto> list(Pageable pageable) {
        return overrideRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public UserPermissionOverrideDto get(Long id) {
        return overrideRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("UserPermissionOverride", id));
    }

    @Transactional
    public UserPermissionOverrideDto create(UserPermissionOverrideRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BadRequestException("User not found", Map.of("userId", request.userId())));
        if (!user.getTenant().getId().equals(tenant.getId())) {
            throw new BadRequestException("Tenant mismatch for user", Map.of("tenantId", request.tenantId()));
        }

        UserPermissionOverride override = new UserPermissionOverride();
        override.setTenant(tenant);
        override.setUser(user);
        applyRequest(override, request);
        return toDto(overrideRepository.save(override));
    }

    @Transactional
    public UserPermissionOverrideDto update(Long id, UserPermissionOverrideRequest request) {
        UserPermissionOverride override = overrideRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UserPermissionOverride", id));

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new BadRequestException("User not found", Map.of("userId", request.userId())));
        if (!user.getTenant().getId().equals(tenant.getId())) {
            throw new BadRequestException("Tenant mismatch for user", Map.of("tenantId", request.tenantId()));
        }

        override.setTenant(tenant);
        override.setUser(user);
        applyRequest(override, request);
        return toDto(override);
    }

    @Transactional
    public void delete(Long id) {
        if (!overrideRepository.existsById(id)) {
            throw new ResourceNotFoundException("UserPermissionOverride", id);
        }
        overrideRepository.deleteById(id);
    }

    private void applyRequest(UserPermissionOverride override, UserPermissionOverrideRequest request) {
        override.setResource(request.resource());
        override.setAction(request.action());
        override.setFeature(request.feature());
        override.setEffect(request.effect());
        if (request.priority() != null) {
            override.setPriority(request.priority());
        }
        override.setReason(request.reason());
        override.setValidFrom(request.validFrom());
        override.setValidUntil(request.validUntil());
        if (request.approved() != null) {
            override.setApproved(request.approved());
        }
        if (request.dualApprovalRequired() != null) {
            override.setDualApprovalRequired(request.dualApprovalRequired());
        }
        override.setRequestedBy(request.requestedBy());
        override.setApprovedBy(request.approvedBy());
        override.setApprovedAt(request.approvedAt());
    }

    private UserPermissionOverrideDto toDto(UserPermissionOverride override) {
        return new UserPermissionOverrideDto(
                override.getId(),
                override.getTenant() != null ? override.getTenant().getId() : null,
                override.getUser() != null ? override.getUser().getId() : null,
                override.getResource(),
                override.getAction(),
                override.getFeature(),
                override.getEffect(),
                override.getPriority(),
                override.getReason(),
                override.getValidFrom(),
                override.getValidUntil(),
                override.isApproved(),
                override.isDualApprovalRequired(),
                override.getRequestedBy(),
                override.getApprovedBy(),
                override.getApprovedAt()
        );
    }
}
