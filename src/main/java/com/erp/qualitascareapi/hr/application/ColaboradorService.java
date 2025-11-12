package com.erp.qualitascareapi.hr.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.hr.api.dto.ColaboradorDto;
import com.erp.qualitascareapi.hr.api.dto.ColaboradorRequest;
import com.erp.qualitascareapi.hr.domain.Cargo;
import com.erp.qualitascareapi.hr.domain.Colaborador;
import com.erp.qualitascareapi.hr.enums.ColaboradorStatus;
import com.erp.qualitascareapi.hr.repo.CargoRepository;
import com.erp.qualitascareapi.hr.repo.ColaboradorRepository;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.repo.SetorRepository;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.iam.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class ColaboradorService {

    private final ColaboradorRepository colaboradorRepository;
    private final TenantRepository tenantRepository;
    private final SetorRepository setorRepository;
    private final CargoRepository cargoRepository;
    private final UserRepository userRepository;

    public ColaboradorService(ColaboradorRepository colaboradorRepository,
                              TenantRepository tenantRepository,
                              SetorRepository setorRepository,
                              CargoRepository cargoRepository,
                              UserRepository userRepository) {
        this.colaboradorRepository = colaboradorRepository;
        this.tenantRepository = tenantRepository;
        this.setorRepository = setorRepository;
        this.cargoRepository = cargoRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<ColaboradorDto> list(Long tenantId, ColaboradorStatus status, Pageable pageable) {
        Page<Colaborador> page;
        if (tenantId != null && status != null) {
            page = colaboradorRepository.findAllByTenant_IdAndStatus(tenantId, status, pageable);
        } else if (tenantId != null) {
            page = colaboradorRepository.findAllByTenant_Id(tenantId, pageable);
        } else if (status != null) {
            page = colaboradorRepository.findAllByStatus(status, pageable);
        } else {
            page = colaboradorRepository.findAll(pageable);
        }
        return page.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public ColaboradorDto get(Long id) {
        return colaboradorRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador", id));
    }

    @Transactional
    public ColaboradorDto create(ColaboradorRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.tenantId()));

        Setor setor = setorRepository.findById(request.setorId())
                .orElseThrow(() -> new ResourceNotFoundException("Setor", request.setorId()));
        validateSameTenant("setorId", setor.getTenant(), tenant);

        Cargo cargo = cargoRepository.findById(request.cargoId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", request.cargoId()));
        validateSameTenant("cargoId", cargo.getTenant(), tenant);

        User usuarioSistema = resolveUsuarioSistema(request.usuarioSistemaId(), tenant.getId());

        String matricula = normalize(request.matricula());
        String cpf = normalize(request.cpf());
        validateUnique(tenant.getId(), matricula, cpf, null);

        Colaborador colaborador = new Colaborador();
        colaborador.setTenant(tenant);
        applyRequest(request, colaborador, matricula, cpf, setor, cargo, usuarioSistema);

        return toDto(colaboradorRepository.save(colaborador));
    }

    @Transactional
    public ColaboradorDto update(Long id, ColaboradorRequest request) {
        Colaborador colaborador = colaboradorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Colaborador", id));

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.tenantId()));

        Setor setor = setorRepository.findById(request.setorId())
                .orElseThrow(() -> new ResourceNotFoundException("Setor", request.setorId()));
        validateSameTenant("setorId", setor.getTenant(), tenant);

        Cargo cargo = cargoRepository.findById(request.cargoId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo", request.cargoId()));
        validateSameTenant("cargoId", cargo.getTenant(), tenant);

        User usuarioSistema = resolveUsuarioSistema(request.usuarioSistemaId(), tenant.getId());

        String matricula = normalize(request.matricula());
        String cpf = normalize(request.cpf());
        validateUnique(tenant.getId(), matricula, cpf, colaborador.getId());

        colaborador.setTenant(tenant);
        applyRequest(request, colaborador, matricula, cpf, setor, cargo, usuarioSistema);

        return toDto(colaborador);
    }

    @Transactional
    public void delete(Long id) {
        if (!colaboradorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Colaborador", id);
        }
        colaboradorRepository.deleteById(id);
    }

    private void applyRequest(ColaboradorRequest request,
                              Colaborador colaborador,
                              String matricula,
                              String cpf,
                              Setor setor,
                              Cargo cargo,
                              User usuarioSistema) {
        colaborador.setMatricula(matricula);
        colaborador.setNomeCompleto(normalize(request.nomeCompleto()));
        colaborador.setCpf(cpf);
        colaborador.setEmail(normalizeNullable(request.email()));
        colaborador.setTelefone(normalizeNullable(request.telefone()));
        colaborador.setSetor(setor);
        colaborador.setCargo(cargo);
        colaborador.setDataAdmissao(request.dataAdmissao());
        colaborador.setStatus(request.status());
        colaborador.setUsuarioSistema(usuarioSistema);
    }

    private void validateSameTenant(String fieldName, Tenant associatedTenant, Tenant expectedTenant) {
        if (associatedTenant == null || !associatedTenant.getId().equals(expectedTenant.getId())) {
            throw new BadRequestException(
                    "O recurso informado não pertence ao tenant do colaborador.",
                    Map.of(
                            fieldName, associatedTenant != null ? associatedTenant.getId() : null,
                            "tenantId", expectedTenant.getId()
                    )
            );
        }
    }

    private User resolveUsuarioSistema(Long usuarioSistemaId, Long tenantId) {
        if (usuarioSistemaId == null) {
            return null;
        }
        User user = userRepository.findById(usuarioSistemaId)
                .orElseThrow(() -> new ResourceNotFoundException("User", usuarioSistemaId));
        if (user.getTenant() == null || !user.getTenant().getId().equals(tenantId)) {
            throw new BadRequestException(
                    "O usuário informado não pertence ao tenant do colaborador.",
                    Map.of("usuarioSistemaId", usuarioSistemaId, "tenantId", tenantId)
            );
        }
        return user;
    }

    private void validateUnique(Long tenantId, String matricula, String cpf, Long currentId) {
        colaboradorRepository.findByTenant_IdAndMatriculaIgnoreCase(tenantId, matricula)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ApplicationException(
                            HttpStatus.CONFLICT,
                            "colaborador.matricula-duplicada",
                            "Já existe um colaborador com esta matrícula para o tenant informado.",
                            Map.of("tenantId", tenantId, "matricula", matricula)
                    );
                });

        colaboradorRepository.findByTenant_IdAndCpf(tenantId, cpf)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ApplicationException(
                            HttpStatus.CONFLICT,
                            "colaborador.cpf-duplicado",
                            "Já existe um colaborador com este CPF para o tenant informado.",
                            Map.of("tenantId", tenantId, "cpf", cpf)
                    );
                });
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }

    private String normalizeNullable(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private ColaboradorDto toDto(Colaborador colaborador) {
        Tenant tenant = colaborador.getTenant();
        Setor setor = colaborador.getSetor();
        Cargo cargo = colaborador.getCargo();
        User usuarioSistema = colaborador.getUsuarioSistema();
        return new ColaboradorDto(
                colaborador.getId(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getName() : null,
                colaborador.getMatricula(),
                colaborador.getNomeCompleto(),
                colaborador.getCpf(),
                colaborador.getEmail(),
                colaborador.getTelefone(),
                colaborador.getDataAdmissao(),
                colaborador.getStatus(),
                setor != null ? setor.getId() : null,
                setor != null ? setor.getNome() : null,
                cargo != null ? cargo.getId() : null,
                cargo != null ? cargo.getNome() : null,
                usuarioSistema != null ? usuarioSistema.getId() : null,
                usuarioSistema != null ? usuarioSistema.getUsername() : null
        );
    }
}
