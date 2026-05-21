package com.erp.qualitascareapi.ged.application;

import com.erp.qualitascareapi.approval.core.contracts.ApprovableTarget;
import com.erp.qualitascareapi.approval.core.domain.*;
import com.erp.qualitascareapi.approval.core.enums.*;
import com.erp.qualitascareapi.approval.core.repo.*;
import com.erp.qualitascareapi.approval.core.services.ApprovalEngine;
import com.erp.qualitascareapi.common.exception.BadRequestException;
import com.erp.qualitascareapi.common.exception.ResourceNotFoundException;
import com.erp.qualitascareapi.ged.api.dto.*;
import com.erp.qualitascareapi.ged.domain.Document;
import com.erp.qualitascareapi.ged.domain.DocumentVersion;
import com.erp.qualitascareapi.ged.enums.DocumentStatus;
import com.erp.qualitascareapi.ged.repo.DocumentVersionRepository;
import com.erp.qualitascareapi.iam.domain.Setor;
import com.erp.qualitascareapi.iam.domain.Tenant;
import com.erp.qualitascareapi.iam.domain.User;
import com.erp.qualitascareapi.iam.enums.OrgRoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class DocumentApprovalService implements ApprovalEngine {

    private static final String CONDITION_LEGAL_REQUIRED = "DOCUMENT_REQUIRES_LEGAL_OPINION";

    private final ApprovalFlowDefRepository flowRepository;
    private final ApprovalStageDefRepository stageRepository;
    private final ApprovalRequestRepository requestRepository;
    private final ApprovalStepRepository stepRepository;
    private final ApprovalStepDecisionRepository decisionRepository;
    private final ApprovalAuditLogRepository auditLogRepository;
    private final DocumentVersionRepository documentVersionRepository;

    public DocumentApprovalService(ApprovalFlowDefRepository flowRepository,
                                   ApprovalStageDefRepository stageRepository,
                                   ApprovalRequestRepository requestRepository,
                                   ApprovalStepRepository stepRepository,
                                   ApprovalStepDecisionRepository decisionRepository,
                                   ApprovalAuditLogRepository auditLogRepository,
                                   DocumentVersionRepository documentVersionRepository) {
        this.flowRepository = flowRepository;
        this.stageRepository = stageRepository;
        this.requestRepository = requestRepository;
        this.stepRepository = stepRepository;
        this.decisionRepository = decisionRepository;
        this.auditLogRepository = auditLogRepository;
        this.documentVersionRepository = documentVersionRepository;
    }

    @Override
    @Transactional
    public ApprovalRequest start(ApprovableTarget target, User requestedBy) {
        ApprovalFlowDef flow = resolveFlow(target.getTenant(), target.getApprovalDomain());
        ApprovalRequest request = new ApprovalRequest();
        request.setTenant(target.getTenant());
        request.setDomain(target.getApprovalDomain());
        request.setTargetKey(target.getApprovalKey());
        request.setRequestedBy(requestedBy);
        request.setRequestedAt(LocalDateTime.now());
        request.setStatus(ApprovalRequestStatus.EM_ANDAMENTO);
        request.setFlowNameSnapshot(flow.getName());
        request.setScopeSetor(target.getScopeSetor());
        ApprovalRequest saved = requestRepository.save(request);

        List<ApprovalStageDef> stages = stageRepository.findAllByFlowDef_IdOrderByOrderAsc(flow.getId()).stream()
                .filter(stage -> Boolean.TRUE.equals(stage.getEnabled()))
                .filter(stage -> shouldIncludeStage(target, stage))
                .toList();
        for (ApprovalStageDef stage : stages) {
            ApprovalStep step = new ApprovalStep();
            step.setRequest(saved);
            step.setStageOrder(stage.getOrder());
            step.setStageCode(stage.getStageCode());
            step.setRequiredRole(stage.getRequiredRole());
            step.setScopeSetor(Boolean.TRUE.equals(stage.getScopeByTargetSetor()) ? target.getScopeSetor() : null);
            step.setStatus(ApprovalStepStatus.PENDENTE);
            stepRepository.save(step);
        }
        writeAudit(saved, null, "STARTED", requestedBy, "Fluxo iniciado");
        return saved;
    }

    @Override
    @Transactional
    public void decide(Long stepId, User user, ApprovalDecision decision, String comment) {
        decide(stepId, user, decision, comment, null);
    }

    @Override
    @Transactional
    public void decide(Long stepId, User user, ApprovalDecision decision, String comment, String returnToStageCode) {
        ApprovalStep step = stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Etapa de aprovação", stepId));
        ApprovalRequest request = step.getRequest();
        ApprovalStepStatus previousStatus = step.getStatus();
        LocalDateTime now = LocalDateTime.now();

        if (decision == ApprovalDecision.APPROVAR) {
            step.setStatus(ApprovalStepStatus.APROVADA);
            step.setDecision(decision);
            step.setDecidedBy(user);
            step.setDecidedAt(now);
            step.setComment(comment);
            step.setApprovalsCount(step.getApprovalsCount() + 1);
            updateRequestAfterForward(request);
        } else if (decision == ApprovalDecision.PULAR_ETAPA) {
            step.setStatus(ApprovalStepStatus.PULADA);
            step.setDecision(decision);
            step.setDecidedBy(user);
            step.setDecidedAt(now);
            step.setComment(comment);
            updateRequestAfterForward(request);
        } else if (decision == ApprovalDecision.SOLICITAR_AJUSTES) {
            step.setStatus(ApprovalStepStatus.EM_AJUSTE);
            step.setDecision(decision);
            step.setDecidedBy(user);
            step.setDecidedAt(now);
            step.setComment(comment);
            request.setStatus(ApprovalRequestStatus.EM_AJUSTE);
            updateTargetStatus(request, DocumentStatus.EM_AJUSTE);
        } else if (decision == ApprovalDecision.RETORNAR_ETAPA) {
            if (returnToStageCode == null || returnToStageCode.isBlank()) {
                throw new BadRequestException("Informe a etapa para retorno", Map.of("field", "returnToStageCode"));
            }
            ApprovalStep target = stepRepository.findByRequest_IdAndStageCode(request.getId(), returnToStageCode)
                    .orElseThrow(() -> new BadRequestException("Etapa de retorno não encontrada", Map.of("returnToStageCode", returnToStageCode)));
            step.setStatus(ApprovalStepStatus.RETORNADA);
            step.setDecision(decision);
            step.setDecidedBy(user);
            step.setDecidedAt(now);
            step.setComment(comment);
            step.setReturnToStageCode(returnToStageCode);
            resetFromStage(request, target.getStageOrder());
            request.setStatus(ApprovalRequestStatus.EM_ANDAMENTO);
            updateTargetStatus(request, DocumentStatus.EM_APROVACAO);
        } else if (decision == ApprovalDecision.REJEITAR) {
            step.setStatus(ApprovalStepStatus.REJEITADA);
            step.setDecision(decision);
            step.setDecidedBy(user);
            step.setDecidedAt(now);
            step.setComment(comment);
            request.setStatus(ApprovalRequestStatus.REJEITADA);
            updateTargetStatus(request, DocumentStatus.CANCELADO);
        } else if (decision == ApprovalDecision.PUBLICAR) {
            step.setStatus(ApprovalStepStatus.APROVADA);
            step.setDecision(decision);
            step.setDecidedBy(user);
            step.setDecidedAt(now);
            step.setComment(comment);
            request.setStatus(ApprovalRequestStatus.PUBLICADA);
            publishTarget(request, comment);
        }

        ApprovalStepDecision history = new ApprovalStepDecision();
        history.setRequest(request);
        history.setStep(step);
        history.setFromStageCode(step.getStageCode());
        history.setToStageCode(step.getReturnToStageCode());
        history.setDecision(decision);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(step.getStatus());
        history.setDecidedBy(user);
        history.setDecidedAt(now);
        history.setComment(comment);
        decisionRepository.save(history);
        writeAudit(request, step.getStageOrder(), decision.name(), user, comment);
    }

    @Transactional(readOnly = true)
    public ApprovalRequestDto toDto(ApprovalRequest request) {
        return new ApprovalRequestDto(
                request.getId(),
                request.getTenant().getId(),
                request.getDomain().name(),
                request.getTargetKey(),
                request.getStatus(),
                request.getFlowNameSnapshot(),
                request.getRequestedBy().getId(),
                displayName(request.getRequestedBy()),
                request.getRequestedAt(),
                stepRepository.findAllByRequest_IdOrderByStageOrderAsc(request.getId()).stream().map(this::toStepDto).toList()
        );
    }

    @Transactional(readOnly = true)
    public List<ApprovalDecisionDto> history(Long requestId) {
        return decisionRepository.findAllByRequest_IdOrderByDecidedAtAsc(requestId).stream()
                .map(decision -> new ApprovalDecisionDto(
                        decision.getId(),
                        decision.getFromStageCode(),
                        decision.getToStageCode(),
                        decision.getDecision(),
                        decision.getPreviousStatus(),
                        decision.getNewStatus(),
                        decision.getDecidedBy().getId(),
                        displayName(decision.getDecidedBy()),
                        decision.getDecidedAt(),
                        decision.getComment()
                ))
                .toList();
    }

    private ApprovalFlowDef resolveFlow(Tenant tenant, ApprovalDomain domain) {
        return flowRepository.findFirstByTenant_IdAndDomainAndActiveTrueOrderByIdDesc(tenant.getId(), domain)
                .orElseGet(() -> createDefaultDocumentFlow(tenant));
    }

    private ApprovalFlowDef createDefaultDocumentFlow(Tenant tenant) {
        ApprovalFlowDef flow = new ApprovalFlowDef();
        flow.setTenant(tenant);
        flow.setDomain(ApprovalDomain.VERSAO_DOCUMENTO);
        flow.setName("Fluxo padrão de documentos institucionais");
        flow.setActive(Boolean.TRUE);
        ApprovalFlowDef saved = flowRepository.save(flow);

        List<StageSeed> stages = List.of(
                new StageSeed(10, "APROVACAO_SETORIAL", OrgRoleType.GERENCIA_SETOR, true, null),
                new StageSeed(20, "VALIDACAO_GERENCIA_DIRETA", OrgRoleType.GERENCIA_SETOR, true, null),
                new StageSeed(30, "PARECER_JURIDICO", OrgRoleType.JURIDICO, false, CONDITION_LEGAL_REQUIRED),
                new StageSeed(40, "APROVACAO_QUALIDADE", OrgRoleType.QUALIDADE_GERENTE, false, null),
                new StageSeed(50, "APROVACAO_DIRETORIA_DIRETA", OrgRoleType.DIRETOR_TECNICO, false, null),
                new StageSeed(60, "APROVACAO_INSTITUCIONAL", OrgRoleType.DIRETORIA_INSTITUCIONAL, false, null),
                new StageSeed(70, "COLETA_ASSINATURAS", OrgRoleType.QUALIDADE_GERENTE, false, null),
                new StageSeed(80, "PUBLICACAO", OrgRoleType.QUALIDADE_GERENTE, false, null)
        );
        for (StageSeed seed : stages) {
            ApprovalStageDef stage = new ApprovalStageDef();
            stage.setFlowDef(saved);
            stage.setOrder(seed.order());
            stage.setStageCode(seed.code());
            stage.setRequiredRole(seed.role());
            stage.setScopeByTargetSetor(seed.scopeByTargetSetor());
            stage.setEnabled(Boolean.TRUE);
            stage.setOptional(seed.conditionKey() != null);
            stage.setConditionKey(seed.conditionKey());
            stageRepository.save(stage);
        }
        return saved;
    }

    private boolean shouldIncludeStage(ApprovableTarget target, ApprovalStageDef stage) {
        if (!CONDITION_LEGAL_REQUIRED.equals(stage.getConditionKey())) {
            return true;
        }
        if (target instanceof DocumentVersion version) {
            Document document = version.getDocumento();
            return document != null && Boolean.TRUE.equals(document.getNecessitaParecerJuridico());
        }
        return false;
    }

    private void updateRequestAfterForward(ApprovalRequest request) {
        boolean hasPending = stepRepository.findAllByRequest_IdOrderByStageOrderAsc(request.getId()).stream()
                .anyMatch(step -> step.getStatus() == ApprovalStepStatus.PENDENTE);
        if (!hasPending) {
            request.setStatus(ApprovalRequestStatus.APROVADA);
            approveTarget(request);
        } else {
            request.setStatus(ApprovalRequestStatus.EM_ANDAMENTO);
            updateTargetStatus(request, DocumentStatus.EM_APROVACAO);
        }
    }

    private void resetFromStage(ApprovalRequest request, Integer stageOrder) {
        List<ApprovalStep> steps = new ArrayList<>(stepRepository.findAllByRequest_IdOrderByStageOrderAsc(request.getId()));
        steps.stream()
                .filter(candidate -> candidate.getStageOrder() >= stageOrder)
                .forEach(candidate -> {
                    candidate.setStatus(ApprovalStepStatus.PENDENTE);
                    candidate.setDecision(null);
                    candidate.setDecidedBy(null);
                    candidate.setDecidedAt(null);
                    candidate.setComment(null);
                    candidate.setReturnToStageCode(null);
                });
    }

    private void approveTarget(ApprovalRequest request) {
        DocumentVersion version = resolveDocumentVersion(request);
        version.setStatus(DocumentStatus.APROVADO);
        version.setAprovadoEm(LocalDateTime.now());
        version.getDocumento().setStatus(DocumentStatus.APROVADO);
    }

    private void publishTarget(ApprovalRequest request, String comment) {
        DocumentVersion version = resolveDocumentVersion(request);
        version.setStatus(DocumentStatus.PUBLICADO);
        version.setPublicadoEm(LocalDateTime.now());
        version.setObservacoesPublicacao(comment);
        version.getDocumento().setStatus(DocumentStatus.PUBLICADO);
        version.getDocumento().setVersaoAtual(version);
        version.getDocumento().setDataVigenciaInicio(version.getDataVigenciaInicio());
        version.getDocumento().setDataVigenciaFim(version.getDataVigenciaFim());
    }

    private void updateTargetStatus(ApprovalRequest request, DocumentStatus status) {
        DocumentVersion version = resolveDocumentVersion(request);
        version.setStatus(status);
        version.getDocumento().setStatus(status);
    }

    private DocumentVersion resolveDocumentVersion(ApprovalRequest request) {
        String prefix = "docVersion:";
        if (request.getTargetKey() == null || !request.getTargetKey().startsWith(prefix)) {
            throw new BadRequestException("Alvo de aprovação inválido", Map.of("targetKey", request.getTargetKey()));
        }
        Long versionId = Long.valueOf(request.getTargetKey().substring(prefix.length()));
        return documentVersionRepository.findById(versionId)
                .orElseThrow(() -> new ResourceNotFoundException("Versão do documento", versionId));
    }

    private ApprovalStepDto toStepDto(ApprovalStep step) {
        return new ApprovalStepDto(
                step.getId(),
                step.getStageOrder(),
                step.getStageCode(),
                step.getRequiredRole() != null ? step.getRequiredRole().name() : null,
                step.getScopeSetor() != null ? step.getScopeSetor().getId() : null,
                step.getScopeSetor() != null ? step.getScopeSetor().getNome() : null,
                step.getStatus(),
                step.getDecision(),
                step.getDecidedBy() != null ? step.getDecidedBy().getId() : null,
                step.getDecidedBy() != null ? displayName(step.getDecidedBy()) : null,
                step.getDecidedAt(),
                step.getComment(),
                step.getReturnToStageCode()
        );
    }

    private void writeAudit(ApprovalRequest request, Integer stepOrder, String event, User who, String data) {
        ApprovalAuditLog log = new ApprovalAuditLog();
        log.setRequest(request);
        log.setStepOrder(stepOrder);
        log.setEvent(event);
        log.setWhenOccurred(LocalDateTime.now());
        log.setWho(who);
        log.setData(data);
        auditLogRepository.save(log);
    }

    private String displayName(User user) {
        return user.getFullName() != null && !user.getFullName().isBlank() ? user.getFullName() : user.getUsername();
    }

    private record StageSeed(Integer order, String code, OrgRoleType role, Boolean scopeByTargetSetor, String conditionKey) {
    }
}
