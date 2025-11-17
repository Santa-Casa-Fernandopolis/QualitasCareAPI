package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.api.dto.*;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import com.erp.qualitascareapi.security.domain.Policy;
import com.erp.qualitascareapi.security.domain.PolicyCondition;
import com.erp.qualitascareapi.security.domain.Role;
import com.erp.qualitascareapi.security.repo.PolicyRepository;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public PolicyService(PolicyRepository policyRepository,
                         TenantRepository tenantRepository,
                         RoleRepository roleRepository,
                         TenantScopeGuard tenantScopeGuard) {
        this.policyRepository = policyRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<PolicyDto> list(Pageable pageable) {
        Long tenantId = requireTenant();
        return policyRepository.findAllByTenant_Id(tenantId, pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public PolicyDto get(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", id));
        tenantScopeGuard.checkTenantAccess(policy.getTenant() != null ? policy.getTenant().getId() : null);
        return toDto(policy);
    }

    @Transactional
    public PolicyDto create(PolicyRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new BadRequestException("Tenant not found", Map.of("tenantId", request.tenantId())));
        tenantScopeGuard.checkRequestedTenant(tenant.getId());

        Policy policy = new Policy();
        policy.setTenant(tenant);
        applyRequest(policy, tenant.getId(), request);
        return toDto(policyRepository.save(policy));
    }

    @Transactional
    public PolicyDto update(Long id, PolicyRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", id));
        Long tenantId = policy.getTenant() != null ? policy.getTenant().getId() : null;
        if (tenantId == null) {
            throw new BadRequestException("Policy tenant not defined", Map.of("policyId", id));
        }
        tenantScopeGuard.checkTenantAccess(tenantId);
        tenantScopeGuard.checkRequestedTenant(request.tenantId());
        if (!tenantId.equals(request.tenantId())) {
            throw new BadRequestException("Tenant mismatch for policy",
                    Map.of("policyId", id, "policyTenantId", tenantId, "requestTenantId", request.tenantId()));
        }
        applyRequest(policy, tenantId, request);
        return toDto(policy);
    }

    @Transactional
    public void delete(Long id) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Policy", id));
        tenantScopeGuard.checkTenantAccess(policy.getTenant() != null ? policy.getTenant().getId() : null);
        policyRepository.delete(policy);
    }

    private void applyRequest(Policy policy, Long tenantId, PolicyRequest request) {
        policy.setResource(request.resource());
        policy.setAction(request.action());
        policy.setFeature(request.feature());
        policy.setEffect(request.effect());
        if (request.enabled() != null) {
            policy.setEnabled(request.enabled());
        }
        if (request.priority() != null) {
            policy.setPriority(request.priority());
        }
        policy.setDescription(request.description());

        if (request.roleIds() != null) {
            Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.roleIds()));
            if (roles.size() != request.roleIds().size()) {
                throw new BadRequestException("Some roles were not found", Map.of("roleIds", request.roleIds()));
            }
            boolean invalidTenant = roles.stream().anyMatch(role -> !role.getTenant().getId().equals(tenantId));
            if (invalidTenant) {
                throw new BadRequestException("Role tenant mismatch", Map.of("tenantId", tenantId));
            }
            policy.getRoles().clear();
            policy.getRoles().addAll(roles);
        }

        if (request.conditions() != null) {
            policy.getConditions().clear();
            List<PolicyCondition> newConditions = request.conditions().stream()
                    .map(condReq -> {
                        PolicyCondition condition = new PolicyCondition();
                        condition.setPolicy(policy);
                        condition.setType(condReq.type());
                        condition.setOperator(condReq.operator());
                        condition.setValue(condReq.value());
                        return condition;
                    })
                    .toList();
            policy.getConditions().addAll(newConditions);
        }
    }

    private PolicyDto toDto(Policy policy) {
        Set<PolicyRoleDto> roles = policy.getRoles().stream()
                .map(role -> new PolicyRoleDto(role.getId(), role.getName()))
                .collect(Collectors.toSet());
        List<PolicyConditionDto> conditions = policy.getConditions().stream()
                .map(cond -> new PolicyConditionDto(cond.getId(), cond.getType(), cond.getOperator(), cond.getValue()))
                .toList();
        Tenant tenant = policy.getTenant();
        return new PolicyDto(policy.getId(), tenant != null ? tenant.getId() : null,
                policy.getResource(), policy.getAction(), policy.getFeature(), policy.getEffect(),
                policy.isEnabled(), policy.getPriority(), policy.getDescription(), roles, conditions);
    }

    private Long requireTenant() {
        Long tenantId = tenantScopeGuard.currentTenantId();
        if (tenantId == null) {
            throw new AccessDeniedException("Tenant context not available");
        }
        return tenantId;
    }
}
