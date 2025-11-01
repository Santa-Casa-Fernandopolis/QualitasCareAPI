package com.erp.qualitascareapi.security.application;

import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.api.dto.*;
import com.erp.qualitascareapi.security.domains.Policy;
import com.erp.qualitascareapi.security.domains.PolicyCondition;
import com.erp.qualitascareapi.security.domains.Role;
import com.erp.qualitascareapi.security.repo.PolicyRepository;
import com.erp.qualitascareapi.security.repo.RoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final TenantRepository tenantRepository;
    private final RoleRepository roleRepository;

    public PolicyService(PolicyRepository policyRepository,
                         TenantRepository tenantRepository,
                         RoleRepository roleRepository) {
        this.policyRepository = policyRepository;
        this.tenantRepository = tenantRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public Page<PolicyDto> list(Pageable pageable) {
        return policyRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional(readOnly = true)
    public PolicyDto get(Long id) {
        return policyRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
    }

    @Transactional
    public PolicyDto create(PolicyRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant not found"));

        Policy policy = new Policy();
        policy.setTenant(tenant);
        applyRequest(policy, tenant.getId(), request);
        return toDto(policyRepository.save(policy));
    }

    @Transactional
    public PolicyDto update(Long id, PolicyRequest request) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found"));
        Long tenantId = policy.getTenant().getId();
        applyRequest(policy, tenantId, request);
        return toDto(policy);
    }

    @Transactional
    public void delete(Long id) {
        if (!policyRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Policy not found");
        }
        policyRepository.deleteById(id);
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
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Some roles were not found");
            }
            boolean invalidTenant = roles.stream().anyMatch(role -> !role.getTenant().getId().equals(tenantId));
            if (invalidTenant) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role tenant mismatch");
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
}
