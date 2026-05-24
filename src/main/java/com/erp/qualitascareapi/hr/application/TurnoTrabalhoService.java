package com.erp.qualitascareapi.hr.application;

import com.erp.qualitascareapi.common.exception.ApplicationException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.hr.api.dto.TurnoTrabalhoDto;
import com.erp.qualitascareapi.hr.api.dto.TurnoTrabalhoRequest;
import com.erp.qualitascareapi.hr.domain.TurnoTrabalho;
import com.erp.qualitascareapi.hr.repo.TurnoTrabalhoRepository;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.repo.TenantRepository;
import com.erp.qualitascareapi.security.application.TenantScopeGuard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Map;

@Service
public class TurnoTrabalhoService {

    private final TurnoTrabalhoRepository turnoRepository;
    private final TenantRepository tenantRepository;
    private final TenantScopeGuard tenantScopeGuard;

    public TurnoTrabalhoService(TurnoTrabalhoRepository turnoRepository,
                                TenantRepository tenantRepository,
                                TenantScopeGuard tenantScopeGuard) {
        this.turnoRepository = turnoRepository;
        this.tenantRepository = tenantRepository;
        this.tenantScopeGuard = tenantScopeGuard;
    }

    @Transactional(readOnly = true)
    public Page<TurnoTrabalhoDto> list(Long tenantId, Pageable pageable) {
        Long contextTenantId = tenantScopeGuard.currentTenantId();
        Long effectiveTenantId = contextTenantId != null ? contextTenantId : tenantId;
        Page<TurnoTrabalho> turnos = effectiveTenantId != null
                ? turnoRepository.findAllByTenant_Id(effectiveTenantId, pageable)
                : turnoRepository.findAll(pageable);
        return turnos.map(this::toDto);
    }

    @Transactional(readOnly = true)
    public TurnoTrabalhoDto get(Long id) {
        return turnoRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Turno de trabalho", id));
    }

    @Transactional
    public TurnoTrabalhoDto create(TurnoTrabalhoRequest request) {
        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.tenantId()));

        String codigoNormalizado = normalize(request.codigo());
        validateUniqueCodigo(tenant.getId(), codigoNormalizado, null);

        TurnoTrabalho turno = new TurnoTrabalho();
        turno.setTenant(tenant);
        applyRequest(request, turno, codigoNormalizado);

        return toDto(turnoRepository.save(turno));
    }

    @Transactional
    public TurnoTrabalhoDto update(Long id, TurnoTrabalhoRequest request) {
        TurnoTrabalho turno = turnoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Turno de trabalho", id));

        Tenant tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant", request.tenantId()));

        String codigoNormalizado = normalize(request.codigo());
        validateUniqueCodigo(tenant.getId(), codigoNormalizado, turno.getId());

        turno.setTenant(tenant);
        applyRequest(request, turno, codigoNormalizado);

        return toDto(turno);
    }

    @Transactional
    public void delete(Long id) {
        if (!turnoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Turno de trabalho", id);
        }
        turnoRepository.deleteById(id);
    }

    private void applyRequest(TurnoTrabalhoRequest request, TurnoTrabalho turno, String codigoNormalizado) {
        boolean cruzaMeiaNoite = Boolean.TRUE.equals(request.cruzaMeiaNoite());
        int intervalo = request.intervaloMinutos() != null ? request.intervaloMinutos() : 0;
        int carga = calculateCargaHorariaMinutos(request.horaInicio(), request.horaFim(), cruzaMeiaNoite, intervalo);
        if (carga <= 0) {
            throw new ApplicationException(
                    HttpStatus.BAD_REQUEST,
                    "turno.horario-invalido",
                    "O horário informado não gera uma carga horária válida.",
                    Map.of("horaInicio", request.horaInicio(), "horaFim", request.horaFim())
            );
        }

        turno.setCodigo(codigoNormalizado);
        turno.setNome(normalize(request.nome()));
        turno.setHoraInicio(request.horaInicio());
        turno.setHoraFim(request.horaFim());
        turno.setCruzaMeiaNoite(cruzaMeiaNoite);
        turno.setIntervaloMinutos(intervalo);
        turno.setActive(request.active() == null || request.active());
        turno.setDescricao(StringUtils.hasText(request.descricao()) ? request.descricao().trim() : null);
    }

    private int calculateCargaHorariaMinutos(LocalTime inicio, LocalTime fim, boolean cruzaMeiaNoite, int intervaloMinutos) {
        long minutos = Duration.between(inicio, fim).toMinutes();
        if (cruzaMeiaNoite && minutos <= 0) {
            minutos += 24L * 60L;
        }
        return Math.toIntExact(minutos - Math.max(intervaloMinutos, 0));
    }

    private String normalize(String value) {
        return value != null ? value.trim() : null;
    }

    private void validateUniqueCodigo(Long tenantId, String codigo, Long currentId) {
        turnoRepository.findByTenant_IdAndCodigoIgnoreCase(tenantId, codigo)
                .filter(existing -> currentId == null || !existing.getId().equals(currentId))
                .ifPresent(existing -> {
                    throw new ApplicationException(
                            HttpStatus.CONFLICT,
                            "turno.codigo-duplicado",
                            "Já existe um turno de trabalho com este código para o tenant informado.",
                            Map.of("tenantId", tenantId, "codigo", codigo)
                    );
                });
    }

    private TurnoTrabalhoDto toDto(TurnoTrabalho turno) {
        Tenant tenant = turno.getTenant();
        Integer intervalo = turno.getIntervaloMinutos() != null ? turno.getIntervaloMinutos() : 0;
        return new TurnoTrabalhoDto(
                turno.getId(),
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getName() : null,
                turno.getCodigo(),
                turno.getNome(),
                turno.getHoraInicio(),
                turno.getHoraFim(),
                turno.isCruzaMeiaNoite(),
                intervalo,
                calculateCargaHorariaMinutos(turno.getHoraInicio(), turno.getHoraFim(), turno.isCruzaMeiaNoite(), intervalo),
                turno.isActive(),
                turno.getDescricao()
        );
    }
}
