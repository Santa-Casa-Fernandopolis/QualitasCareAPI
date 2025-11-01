# API de Observabilidade e Trilhas de Auditoria

Este documento descreve os recursos expostos pela QualitasCare API para consultar logs operacionais, auditoria de segurança e trilhas de dados construídas com Hibernate Envers. As rotas foram desenhadas para atender requisitos de rastreabilidade, conformidade LGPD/ONA e investigações rápidas de incidentes clínicos/administrativos.

## Visão Geral

| Área | Endpoint | Descrição |
| --- | --- | --- |
| Logs Operacionais | `GET /api/logs/operational` | Consulta requisições HTTP registradas pelo filtro de access log estruturado. |
| Auditoria de Segurança | `GET /api/logs/security` | Lista eventos de autenticação/autorização capturados pelos listeners do Spring Security. |
| Auditoria de Dados | `GET /api/logs/data/{entidade}/{id}` | Recupera o histórico de revisões das entidades anotadas com `@Audited`. |

Todos os endpoints exigem autenticação (mesma política das demais APIs) e respeitam paginação/filtragem para facilitar investigações.

---

## Logs Operacionais (`/api/logs/operational`)

Permite analisar requisições HTTP registradas pelo `RequestLoggingFilter`, que persiste traceId, usuário, IP, status e latência de cada chamada (exceto `/actuator/**` e `/error`).

### Parâmetros de Consulta

| Parâmetro | Tipo | Descrição |
| --- | --- | --- |
| `from`, `to` | `Instant` (ISO-8601) | Limita o intervalo de tempo. |
| `method` | `String` | Filtra por método HTTP (GET, POST, ...). |
| `status` | `Integer` | Filtra por código de status HTTP. |
| `userId` | `String` | Filtra por usuário autenticado. |
| `traceId` | `String` | Retorna somente a jornada correlacionada a um trace específico. |
| `path` | `String` | Busca parcial por URI. |
| `page`, `size` | `Integer` | Paginação padrão Spring (default 0/20). |

### Exemplo de Resposta

```json
{
  "content": [
    {
      "id": 42,
      "timestamp": "2025-01-01T12:34:56.789Z",
      "method": "POST",
      "path": "/api/non-conformities",
      "status": 201,
      "durationMs": 134,
      "traceId": "d3b4...",
      "userId": "joana.souza",
      "clientIp": "10.1.2.3",
      "httpVersion": "HTTP/1.1",
      "contentLength": 512
    }
  ],
  "pageable": { ... },
  "totalElements": 1,
  "totalPages": 1
}
```

---

## Auditoria de Segurança (`/api/logs/security`)

Lista eventos gerados pelos listeners `SecurityAuditEventListener`, que persistem sucessos/falhas de autenticação e falhas de autorização com carimbo de tempo, usuário e IP.

### Parâmetros de Consulta

| Parâmetro | Tipo | Descrição |
| --- | --- | --- |
| `from`, `to` | `Instant` | Intervalo temporal (ISO-8601). |
| `username` | `String` | Usuário afetado. |
| `eventType` | `SecurityAuditEventType` | `AUTHENTICATION_SUCCESS`, `AUTHENTICATION_FAILURE` ou `AUTHORIZATION_FAILURE`. |
| `traceId` | `String` | Correlação direta com logs/traces. |
| `page`, `size` | `Integer` | Paginação padrão. |

### Exemplo de Resposta

```json
{
  "content": [
    {
      "id": 10,
      "timestamp": "2025-01-02T08:15:30.000Z",
      "username": "carlos.admin",
      "eventType": "AUTHORIZATION_FAILURE",
      "clientIp": "10.2.3.4",
      "traceId": "a1b2...",
      "description": "Acesso negado ao recurso org.springframework.security.access.intercept.AbstractSecurityInterceptor"
    }
  ],
  "totalElements": 8,
  "totalPages": 1
}
```

---

## Auditoria de Dados (`/api/logs/data/{entidade}/{id}`)

Expõe o histórico de revisões produzido pelo Hibernate Envers para entidades anotadas com `@Audited`. A URL usa o **nome simples** da entidade (case-insensitive) seguido do identificador primário.

### Exemplo

```
GET /api/logs/data/User/15
```

### Resposta

```json
[
  {
    "revisionId": 71,
    "timestamp": "2025-01-05T09:20:00.000Z",
    "username": "maria.coordenadora",
    "clientIp": "10.1.5.20",
    "state": {
      "id": 15,
      "username": "enfermeira.luiza",
      "status": "ACTIVE",
      "department": "UTI",
      "updatedAt": "2025-01-05T09:19:58.000",
      "tenant": {
        "id": 3,
        "name": "Hospital Filantrópico" }
    }
  }
]
```

> **Observações:**
> - Os relacionamentos são serializados conforme configurado no Jackson (`@JsonIgnore`, `@JsonManagedReference`, etc.).
> - IDs compostos não são suportados nesta versão.

---

## Estrutura Técnica

- `CorrelationFilter` popula `traceId`, `clientIp` e `userId` no MDC para todos os logs.
- `RequestLoggingFilter` persiste access logs na tabela `request_logs`.
- `SecurityAuditEventListener` salva eventos de segurança na tabela `security_audit_events`.
- Entidades críticas (`User`, `Tenant`, `Permission`, `Policy`, `Role`, etc.) foram anotadas com `@Audited` para gerar históricos no Envers.
- `AuditRevisionEntity` captura usuário/IP de cada revisão, apoiado pelo `AuditRevisionListener`.

---

## Boas Práticas e Próximos Passos

1. **Retenção**: defina políticas de limpeza para `request_logs` e `security_audit_events` (ex.: 90 dias online, arquivamento posterior).
2. **Indexação**: avalie criar índices adicionais em colunas consultadas com frequência (`status`, `path`).
3. **Exportação Imutável**: utilize jobs periódicos para extrair revisões/relatórios e enviar para armazenamento WORM (S3 Object Lock).
4. **Integração com Observabilidade Externa**: configure agentes/forwarders para enviar os mesmos logs JSON para OpenSearch/ELK e spans para Jaeger/Tempo.
5. **Mascaramento LGPD**: revise `RequestLoggingFilter` e listeners para evitar armazenar dados pessoais sensíveis em `description` ou `state`.

Com esses recursos, a equipe de Qualidade e TI passa a contar com um painel completo para reconstruir jornadas técnicas, comprovar alterações e monitorar incidentes de segurança.
