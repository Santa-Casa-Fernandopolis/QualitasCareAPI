package com.erp.qualitascareapi.observability.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.EntityType;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class DataAuditQueryService {

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public DataAuditQueryService(EntityManager entityManager, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<DataAuditEntry> getRevisions(String entityAlias, String rawId) {
        Class<?> entityClass = resolveEntityClass(entityAlias)
                .orElseThrow(() -> new IllegalArgumentException("Entidade auditada não encontrada: " + entityAlias));
        Object identifier = convertIdentifier(entityClass, rawId);

        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<Number> revisionNumbers = reader.getRevisions(entityClass, identifier);
        return revisionNumbers.stream()
                .map(revisionNumber -> toEntry(reader, entityClass, identifier, revisionNumber))
                .toList();
    }

    private DataAuditEntry toEntry(AuditReader reader, Class<?> entityClass, Object identifier, Number revisionNumber) {
        AuditRevisionEntity revisionEntity = reader.findRevision(AuditRevisionEntity.class, revisionNumber);
        Object entity = reader.find(entityClass, identifier, revisionNumber);
        Map<String, Object> state = objectMapper.convertValue(entity, Map.class);
        return new DataAuditEntry(revisionEntity.getId(), Instant.ofEpochMilli(revisionEntity.getTimestamp()),
                revisionEntity.getUsername(), revisionEntity.getClientIp(), state);
    }

    private Optional<Class<?>> resolveEntityClass(String alias) {
        for (EntityType<?> entity : entityManager.getMetamodel().getEntities()) {
            if (entity.getJavaType().getSimpleName().equalsIgnoreCase(alias)
                    || entity.getName().equalsIgnoreCase(alias)) {
                return Optional.of(entity.getJavaType());
            }
        }
        return Optional.empty();
    }

    private Object convertIdentifier(Class<?> entityClass, String rawId) {
        var metamodel = entityManager.getMetamodel().entity(entityClass);
        Class<?> idType = metamodel.getIdType().getJavaType();
        if (Long.class.equals(idType)) {
            return Long.valueOf(rawId);
        }
        if (Integer.class.equals(idType)) {
            return Integer.valueOf(rawId);
        }
        if (String.class.equals(idType)) {
            return rawId;
        }
        throw new IllegalArgumentException("Tipo de identificador não suportado: " + idType.getSimpleName());
    }
}
