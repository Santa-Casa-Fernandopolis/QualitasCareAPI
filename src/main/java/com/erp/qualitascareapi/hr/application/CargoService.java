package com.erp.qualitascareapi.hr.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.hr.api.dto.CargoDto;
import com.erp.qualitascareapi.hr.api.dto.CargoRequest;
import com.erp.qualitascareapi.hr.domain.Cargo;
import com.erp.qualitascareapi.hr.repo.CargoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class CargoService {

    private final CargoRepository cargoRepository;
    private final TenantRepository tenantRepository;

    public CargoService(CargoRepository cargoRepository, TenantRepository tenantRepository) {
        this.cargoRepository = cargoRepository;
        this.tenantRepository = tenantRepository;
    }

    @Transactional(readOnly = true)
    public Page<CargoDto> list(Long tenantId, Pageable pageable) {
        Page<Cargo> cargos = tenantId != null
                ? cargoRepository.findAllByTenant_Id(tenantId, pageable)
                : cargoRepository.findAll(pageable);
        return cargos.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public CargoDto get(Long id) {
        return cargoRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", id));
    }

    @Transactional
    public CargoDto create(CargoRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.tenantId()));

        String codigoNormalizado = normalize(request.codigo());
        validateUniqueCodigo(tenant.getId(), codigoNormalizado, null);

        Cargo cargo = new Cargo();
        cargo.setTenant(tenant);
        applyRequest(request, cargo, codigoNormalizado);

        return toDto(cargoRepository.save(cargo));
    }

    @Transactional
    public CargoDto update(Long id, CargoRequest request) {
        Cargo cargo = cargoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", id));

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.tenantId()));

        String codigoNormalizado = normalize(request.codigo());
        validateUniqueCodigo(tenant.getId(), codigoNormalizado, cargo.getId());

        cargo.setTenant(tenant);
        applyRequest(request, cargo, codigoNormalizado);

        return toDto(cargo);
    }

    @Transactional
    public void delete(Long id) {
        if (!cargoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cargo", id);
        }
        cargoRepository.deleteById(id);
    }

    private void applyRequest(CargoRequest request, Cargo cargo, String codigoNormalizado) {
        cargo.setCodigo(codigoNormalizado);
        cargo.setNome(normalize(request.nome()));
        cargo.setDescricao(StringUtils.hasText(request.descricao()) ? request.descricao().trim() : null);
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }

    private void validateUniqueCodigo(Long tenantId, String codigo, Long currentId) {
        cargoRepository.findByTenant_IdAndCodigoIgnoreCase(tenantId, codigo)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ApplicationException(
                            HttpStatus.CONFLICT,
                            "cargo.codigo-duplicado",
                            "Já existe um cargo com este código para o tenant informado.",
                            Map.of(
                                    "tenantId", tenantId,
                                    "codigo", codigo
                            )
                    );
                });
    }

    private CargoDto toDto(Cargo cargo) {
        Tenant tenant = cargo.getTenant();
        return new CargoDto(
                cargo.getId(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getName() : null,
                cargo.getCodigo(),
                cargo.getNome(),
                cargo.getDescricao()
        );
    }
}
