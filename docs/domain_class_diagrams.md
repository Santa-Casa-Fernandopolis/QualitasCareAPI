# Domain Class Diagrams

Este documento descreve a **arquitetura de domínio** do QualitasCareAPI a partir do arquivo `domain_class_diagrams.puml`, agrupando agregados por **bounded context** (módulos) e explicitando dependências entre pacotes. O objetivo é facilitar entendimento, manutenção, auditoria (ONA/Anvisa) e evolução do modelo.

> Renderização:
>
> ```bash
> plantuml docs/domain_class_diagrams.puml
> ```
>
> Dica: use `-tpng` ou `-tsvg` para exportar imagens.

---

## Visão geral e princípios

* **Multi-tenant**: quase todas as entidades de negócio possuem `Tenant` (chave estrangeira), isolando dados por hospital/unidade.
* **Auditoria e rastreabilidade**: entidades com `@Audited` (Envers); `EvidenciaArquivo` associa anexos (hash, mimetype) a registros críticos.
* **Enums descritivos**: estados/tipos padronizados (ex.: `LoteStatus`, `CicloStatus`, `NaoConformidadeStatus`) para regras claras e validação simples.
* **Reuso entre módulos**: o pacote `core` concentra classes base (ex.: `Setor`, `Instrumento`, `Kit*`) e é reutilizado por `cme`, `quality` e `environmental`.
* **Segurança e autorização**: fora do escopo deste diagrama, mas as entidades respeitam o modelo ABAC/RBAC (Policies/Permissions/Overrides) do módulo `security`.

---

## Convenções do diagrama

* **Pacotes** = módulos de domínio (`core`, `cme`, `quality`, `environmental`).
* **Setas sólidas**: relacionamentos JPA (`@ManyToOne`, `@ManyToMany`, etc.).
* **Enum**: tipos e estados persistidos via `@Enumerated(EnumType.STRING)`.
* **Estereótipos**: `<<Entity, Audited>>` indica auditoria Envers ativa.

---

# 📘 Documento de Domínio — Sistema **QualitasCareAPI**

Cada pacote representa um **módulo funcional** do sistema e contém suas classes, enums e relacionamentos conforme mapeamento JPA/Hibernate, incluindo observações de auditoria, índices e vínculos entre módulos.

---

## Sumário

1. [Pacote `iam.domain`](#iamdomain)
2. [Pacote `security.enums`](#securityenums)
3. [Pacote `security.domain`](#securitydomain)
4. [Pacote `observability.audit`](#observabilityaudit)
5. [Pacote `observability.logging`](#observabilitylogging)
6. [Pacote `observability.security`](#observabilitysecurity)
7. [Pacote `core.enums`](#coreenums)
8. [Pacote `core.domain`](#coredomain)
9. [Pacote `cme.enums`](#cmeenums)
10. [Pacote `cme.domain`](#cmedomain)
11. [Pacote `environmental.enums`](#environmentalenums)
12. [Pacote `environmental.domain`](#environmentaldomain)
13. [Pacote `quality.enums`](#qualityenums)
14. [Pacote `quality.domain`](#qualitydomain)
15. [Pacote `common.domain`](#commondomain)
16. [Relações entre pacotes](#relações-principais-entre-pacotes)
17. [Regras e guidelines](#regras-de-integridade-e-negócio-guidelines)
18. [Padrões técnicos](#padrões-técnicos-adotados)
19. [Consultas típicas](#exemplos-de-navegação-típica-consultas)
20. [Extensibilidade](#extensibilidade)
21. [Checklist do diagrama](#checklist-de-qualidade-do-diagrama)

---

## iam.domain

### Tenant (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| code | Long | Código interno |
| name | String | Nome da instituição |
| cnpj | String | Identificador nacional |
| active | boolean | Status de atividade |

### User (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| userName | String | Login único por tenant |
| passwordHash | String | Hash seguro |
| fullName | String | Nome completo do usuário |

---

## security.enums

### UserStatus
Estados possíveis do ciclo de vida de um usuário.
| Constante | active |
|---|---|
| PROVISIONED | false |
| ACTIVE | true |
| SUSPENDED | false |
| DISABLED | false |
| EXPIRED | false |

Método: `isActive(): boolean`

### ResourceType
`INDICADOR, AUDITORIA, NC, PROTOCOLO, CAPACITACAO, PGRSS, USUARIO, DASHBOARD`

### IdentityOrigin
`LOCAL, LDAP, SSO, IMPORTED`

> Indica a origem da identidade do usuário, mantida curta para validações e auditorias simples.

### Effect
`ALLOW, DENY`

### Action
`READ, CREATE, UPDATE, DELETE, APPROVE, EXPORT, CLOSE`

---

## security.domain

### Policy (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK |
| resource | ResourceType | Escopo de autorização |
| action | Action | Ação controlada |
| feature | String | **NULL = coringa** (qualquer feature) |
| effect | Effect | ALLOW/DENY |
| enabled | boolean | Default `true` |
| priority | int | Default `100` |
| description | String | Descritivo |

**Índices**
- `idx_policy_scope(tenant_id, resource, action, feature, priority)`
- `idx_policy_enabled(enabled)`

**Relações**
- `Policy *—* Role` (tabela `policy_roles`)
- `Policy 1 o—* PolicyCondition` (`cascade=ALL`, `orphanRemoval=true`)

---

### PolicyCondition (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| policy | Policy | FK |
| type | String | Ex.: `TARGET_DEPARTMENT` |
| operator | String | EQ, NE, IN, NOT_IN |
| value | String | Ex.: `"UTI|CME"` |

---

### Role (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| name | String | **Único por tenant** |
| tenant | Tenant | FK |
| description | String | Descritivo |

**Unique:** `uq_role_tenant_name(tenant_id, name)`

---

### Permission (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| resource | ResourceType | Escopo |
| action | Action | Operação |
| feature | String | **NULL = coringa** |
| tenant | Tenant | FK |
| code | String | Ex.: `"NC_READ@LISTA"` |

**Uniques**
- `uq_perm_scope(tenant_id, resource, action, feature)`
- `uq_perm_code_tenant(tenant_id, code)`

---

### RolePermission (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| role | Role | FK |
| permission | Permission | FK |
| tenant | Tenant | FK |

**Unique:** `uq_role_perm(tenant_id, role_id, permission_id)`

---

### UserPermissionOverride (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| user | User | FK |
| tenant | Tenant | FK |
| resource | ResourceType | Escopo |
| action | Action | Operação |
| feature | String | **NULL = coringa** |
| effect | Effect | ALLOW/DENY |
| priority | int | Default `100` |
| reason | String | Justificativa |
| validFrom | LocalDateTime | Início da validade |
| validUntil | LocalDateTime | Fim da validade |
| approved | boolean | Flag de aprovação |
| dualApprovalRequired | boolean | Aprovação dupla |
| requestedBy | String | Solicitante |
| approvedBy | String | Aprovador |
| approvedAt | LocalDateTime | Data/hora da aprovação |

**Índice:** `idx_override_lookup(tenant_id, user_id, resource, action, feature, priority)`

> **Regra de avaliação:** overrides vencidos são ignorados; entre válidos, aplica-se o de menor `priority`. Em empate, prevalece `DENY` (fail-secure).

---

## observability.audit

### AuditRevisionEntity (Entity, @RevisionEntity)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | @RevisionNumber |
| timestamp | long | @RevisionTimestamp |
| username | String | Usuário que executou a transação |
| clientIp | String | IP do cliente |

**Listener:** `AuditRevisionListener` (preenche username e clientIp via contexto de segurança).

---

## observability.logging

### RequestLog (Entity)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| timestamp | Instant | logged_at |
| method | String | — |
| path | String | — |
| status | int | — |
| durationMs | long | Tempo de resposta |
| traceId | String | Correlaciona com auditoria |
| userId | String | — |
| clientIp | String | — |
| httpVersion | String | — |
| contentLength | Long | — |

**Índices**
- `idx_request_logs_ts(logged_at)`
- `idx_request_logs_user(user_id)`
- `idx_request_logs_trace(trace_id)`

> Correlação conceitual: `RequestLog.traceId` vincula eventos do Envers (`AuditRevisionEntity`) e auditorias de segurança.

---

## observability.security

### SecurityAuditEventType (Enum)
`AUTHENTICATION_SUCCESS, AUTHENTICATION_FAILURE, AUTHORIZATION_FAILURE`

### SecurityAuditEvent (Entity)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| timestamp | Instant | occurred_at |
| username | String | Usuário |
| eventType | SecurityAuditEventType | Tipo do evento |
| clientIp | String | — |
| traceId | String | — |
| description | String | — |

**Índices**
- `idx_sec_audit_ts(occurred_at)`
- `idx_sec_audit_user(username)`
- `idx_sec_audit_type(event_type)`

---

## core.enums
- **ExameCulturaResultado:** `PENDENTE, NEGATIVO, POSITIVO, INVALIDO`
- **TipoSetor:** `CME, CC, UTI, ENFERMARIA, FARMACIA, HOTELARIA, MANUTENCAO, PS`

---

## core.domain

### Setor
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK obrigatório |
| nome | String | 120 |
| tipo | TipoSetor | — |
| descricao | String | — |

### Instrumento
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK obrigatório |
| nome | String | 150 |
| codigoHospitalar | String | — |
| descricao | String | — |

### KitProcedimento
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK obrigatório |
| nome | String | 150 |
| codigo | String | — |
| observacoes | String | — |
| ativo | Boolean | default TRUE |

### KitVersion
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| kit | KitProcedimento | FK |
| numeroVersao | Integer | — |
| vigenciaInicio | LocalDate | — |
| validadeDias | Integer | — |
| ativo | Boolean | default TRUE |
| observacoes | String | — |

### KitItem
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| versao | KitVersion | FK |
| instrumento | Instrumento | FK |
| quantidade | Integer | — |
| observacoes | String | — |

### ExameCultura
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK obrigatório |
| origemAmostra | String | 120 |
| dataColeta | LocalDate | — |
| responsavelColeta | String | 150 |
| resultado | ExameCulturaResultado | Default `PENDENTE` |
| registradoPor | User | — |
| observacoes | String | — |

**Relações**
- `ExameCultura *—* EvidenciaArquivo` (ManyToMany)

---

## cme.enums
- **UsoSaneanteEtapa:** `PRE_LIMPEZA, LIMPEZA_MANUAL, LAVADORA_TERMODESINFECCAO, DESINFECCAO_ALTO_NIVEL`
- **ResultadoConformidade:** `CONFORME, NAO_CONFORME, NAO_APLICAVEL`
- **NaoConformidadeSeveridade:** `BAIXA, MEDIA, ALTA, CRITICA`
- **MovimentacaoTipo:** `ENTRADA_CONTAMINADO, ENVIO_ESTERIL, RETORNO_CONTAMINADO, DESCARTE`
- **ManutencaoTipo:** `PREVENTIVA, CORRETIVA, CALIBRACAO, VERIFICACAO_METROLOGICA`
- **ManutencaoStatus:** `PLANEJADA, ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA`
- **LoteStatus:** `MONTADO, EM_PROCESSO, LIBERADO, BLOQUEADO, VENCIDO`
- **CicloStatus:** `AGENDADO, EM_ANDAMENTO, CONCLUIDO, BLOQUEADO`

---

## cme.domain

| Classe | Relações principais |
|---|---|
| **Autoclave** | Tenant |
| **CicloEsterilizacao** | Tenant, Autoclave, LoteEtiqueta, User (liberadoPor) |
| **LoteEtiqueta** | Tenant, KitVersion, User (montadoPor) |
| **ManutencaoAutoclave** | Autoclave, EvidenciaArquivo (M:N) |
| **PlanoPreventivoAutoclave** | Autoclave |
| **MovimentacaoCME** | Tenant, LoteEtiqueta, Setor (origem/destino), User |
| **UsoSaneante** | SaneantePeraceticoLote, User |
| **SaneantePeraceticoLote** | Tenant |
| **TesteBowieDick** | Autoclave, User, EvidenciaArquivo (M:N) |
| **IndicadorQuimico** | CicloEsterilizacao, EvidenciaArquivo (M:N) |
| **IndicadorBiologico** | CicloEsterilizacao, EvidenciaArquivo (M:N) |
| **HigienizacaoUltrassonica** | Tenant, User, EvidenciaArquivo (M:N) |
| **HigienizacaoAutoclaveProfunda** | Autoclave, User, EvidenciaArquivo (M:N) |
| **NaoConformidadeCME** | Tenant, TipoNaoConformidade, User, EvidenciaArquivo (M:N) |

> Todas as classes da CME são auditadas via Envers e vinculadas a `Tenant`.  
> **Observação:** o código-fonte apresentava duplicidade de `PlanoPreventivoAutoclave`; manter apenas **uma** definição.

---

## environmental.enums
- **ClasseResiduo:** `PERFUROCORTANTE, BIOLOGICO, QUIMICO, RECICLAVEL, COMUM`

---

## environmental.domain

### GeracaoResiduo
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK obrigatório |
| dataRegistro | LocalDate | — |
| classeResiduo | ClasseResiduo | — |
| pesoEstimadoKg | Double | — |
| destinoFinal | String | — |
| loteRelacionada | LoteEtiqueta | — |
| saneanteRelacionado | SaneantePeraceticoLote | — |
| observacoes | String | — |

---

## quality.enums
- **NaoConformidadeStatus:** `ABERTA, EM_INVESTIGACAO, EM_IMPLEMENTACAO, CONCLUIDA, CANCELADA`

---

## quality.domain

### NaoConformidadeBase (Interface)
| Método | Retorno |
|---|---|
| getId() | Long |
| getTenant() | Tenant |
| getTitulo() | String |
| getStatus() | NaoConformidadeStatus |
| getTipo() | TipoNaoConformidade |

### TipoNaoConformidade (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK |
| nome | String | 120 |
| descricao | String | 255 |

---

## common.domain

### EvidenciaArquivo (Entity, Audited)
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK obrigatório |
| nomeArquivo | String | 180 |
| uri | String | 255 |
| hashSha256 | String | 64 |
| contentType | String | 120 |
| tamanhoBytes | Long | — |
| autor | User | FK opcional |
| criadoEm | LocalDateTime | definido no @PrePersist |

**Relações**
- ManyToMany com várias entidades (CME e Core).

**Notas**
- Tabela: `evidencias_arquivo`
- Utilizada para rastreabilidade documental e comprovação de conformidades.

---

## Relações principais entre pacotes

- **Tenant**: FK presente em todas as entidades auditáveis.
- **User**: Referenciado por entidades de autoria ou execução.
- **EvidenciaArquivo**: vínculo ManyToMany em processos de CME e auditorias.
- **Política de acesso (Security)**: define camada de autorização granular (tenant + recurso + ação + feature).
- **Observabilidade**: une logs de requisição, revisões de dados e auditorias de segurança via `traceId`.

---

## Regras de integridade e negócio (guidelines)

1. **Tenant obrigatório** em todas as entidades de negócio “top-level”.
2. **Estados coerentes**:
    * `CicloEsterilizacao.status`: `AGENDADO → EM_ANDAMENTO → CONCLUIDO` (ou `BLOQUEADO`).
    * `LoteEtiqueta.status`: `MONTADO → EM_PROCESSO → LIBERADO` (ou `BLOQUEADO`/`VENCIDO`).
    * `NaoConformidadeStatus`: `ABERTA → EM_INVESTIGACAO → EM_IMPLEMENTACAO → CONCLUIDA` (ou `CANCELADA`).
3. **Validade de lotes**: `LoteEtiqueta.validade` ≥ `dataEmpacotamento`; bloquear se expirar.
4. **Rastreio de saneantes**: `UsoSaneante.etapa` segue a sequência do processo; `volumeUtilizadoMl` ≥ 0.
5. **Indicadores**: se `resultado = NAO_CONFORME`, exigir evidência e ação corretiva (NC ou bloqueio).
6. **Resíduos (PGRSS)**: `classeResiduo` compatível com o insumo/processo de origem.
7. **Evidências**: `hashSha256` imutável; `uri` deve apontar para repositório confiável (ex.: S3 com Object Lock).

---

## Padrões técnicos adotados

* **JPA/Hibernate**: `@ManyToOne(fetch = LAZY)` por padrão; `@Enumerated(EnumType.STRING)`.
* **Auditoria (Envers)**: classes críticas com `@Audited`; trilha em `revinfo` captura `username`/`clientIp`.
* **Observabilidade**: integração com `RequestLog` (acesso), `SecurityAuditEvent` (login/autorização) e **MDC traceId** (correlação).

---

## Exemplos de navegação típica (consultas)

* **Ciclo completo**: `Autoclave → CicloEsterilizacao → {IndicadorQuimico, IndicadorBiologico} → LoteEtiqueta`.
* **Ação corretiva**: `NaoConformidadeCME (NAO_CONFORME) → EvidenciaArquivo`; acionar `PlanoPreventivoAutoclave`/`ManutencaoAutoclave` se necessário.
* **PGRSS**: `GeracaoResiduo` ← (`LoteEtiqueta` | `SaneantePeraceticoLote`) para relatórios de destinação final.

---

## Extensibilidade

* **Novos setores**: ampliar `TipoSetor` sem quebrar o domínio.
* **Novos estados**: adicionar em enums com cautela (migrar dados e regras).
* **CME**: expandir entidades conectadas a `LoteEtiqueta` e `CicloEsterilizacao`.
* **Integrações**: eventos de domínio podem alimentar ETL/ELT para BI/indicadores ONA.

---

## Checklist de qualidade do diagrama

* [x] Todas as entidades de negócio possuem **Tenant** quando aplicável.
* [x] Estados e tipos **persistidos como String**.
* [x] Relacionamentos críticos com **multiplicidades corretas**.
* [x] **Evidências** associadas onde a conformidade exige prova documental.
* [x] Integração **CME ⇄ Quality ⇄ Environmental** desenhada.
* [x] Duplicidades de classe **removidas** (ex.: `PlanoPreventivoAutoclave`).

---

*Gerado em 2025-11-03T17:04:16*
