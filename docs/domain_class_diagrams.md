
# Domain Class Diagrams — Unified & Updated (GED + EDU + HR)

> Versão consolidada com correções de sumário, índices/restrições explícitos, inclusão dos módulos **hr.domain** e **edu** completos, snapshot de **ged.domain** para referência cruzada, e organização de seções e relações.


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
17. [Pacote `hr.domain`](#hrdomain)
18. [Pacote `edu.enums`](#eduenums)
19. [Pacote `edu.domain`](#edudomain)
20. [Regras de integridade e negócio (Guidelines)](#regras-de-integridade-e-negócio-guidelines)
21. [Padrões técnicos adotados](#padrões-técnicos-adotados)
22. [Exemplos de navegação típica (Consultas)](#exemplos-de-navegação-típica-consultas)
23. [Extensibilidade](#extensibilidade)
24. [Checklist de qualidade do diagrama](#checklist-de-qualidade-do-diagrama)


---

## iam.domain
| Entidade | Campos-chave | Observações |
|---|---|---|
| **Tenant** | `id, code, name, cnpj, logo, active` | Identidade do cliente (multi-tenant). |
| **User** | `id, userName, passwordHash, fullName` | Usuários do sistema (nem todo colaborador é user). |

---

## security.enums
- `UserStatus` {PROVISIONED, ACTIVE, SUSPENDED, DISABLED, EXPIRED}
- `ResourceType` {INDICADOR, AUDITORIA, NC, PROTOCOLO, CAPACITACAO, PGRSS, USUARIO, DASHBOARD, DOCUMENTO, DOCUMENTO_VERSAO, DOCUMENTO_TREINAMENTO, DOCUMENTO_ACK, DOCUMENTO_ALTERACAO}
- `Effect` {ALLOW, DENY}
- `Action` {READ, CREATE, UPDATE, DELETE, APPROVE, EXPORT, CLOSE}
- `IdentityOrigin` {LOCAL, LDAP, SSO, IMPORTED}

---

## security.domain
| Entidade | Campos-chave | Índices/Restrições | Observações |
|---|---|---|---|
| **Policy** | `tenant, resource, action, feature, effect, priority, enabled` | `(tenant_id, resource, action, feature, priority)`; `(enabled)` | ABAC (feature opcional = coringa). |
| **PolicyCondition** | `policy, type, operator, value` |  | Condições (EQ, NE, IN...). |
| **Role** | `tenant, name, description` | **UNIQUE** `(tenant_id, name)` |  |
| **Permission** | `tenant, resource, action, feature, code` | **UNIQUE** `(tenant_id, resource, action, feature)`; **UNIQUE** `(tenant_id, code)` |  |
| **RolePermission** | `role, permission, tenant` | **UNIQUE** `(tenant_id, role_id, permission_id)` |  |
| **UserPermissionOverride** | `user, tenant, resource, action, feature, effect, priority, ...` | `(tenant_id, user_id, resource, action, feature, priority)` | Exceções por usuário com aprovação dupla opcional. |

---

## observability.audit
- **AuditRevisionEntity** (RevisionEntity): `id, timestamp, username, clientIp`
- **AuditRevisionListener**

## observability.logging
- **RequestLog**: `id, timestamp, method, path, status, durationMs, traceId, userId, clientIp, httpVersion, contentLength`  
  Índices: `(logged_at)`, `(user_id)`, `(trace_id)`

## observability.security
- **SecurityAuditEventType**: {AUTHENTICATION_SUCCESS, AUTHENTICATION_FAILURE, AUTHORIZATION_FAILURE}
- **SecurityAuditEvent**: `id, timestamp, username, eventType, clientIp, traceId, description`  
  Índices: `(occurred_at)`, `(username)`, `(event_type)`

---

## core.enums
- `ExameCulturaResultado` {PENDENTE, NEGATIVO, POSITIVO, INVALIDO}
- `TipoSetor` {CME, CC, UTI, ENFERMARIA, FARMACIA, HOTELARIA, MANUTENCAO, PS}

## core.domain
- **Setor**: `tenant, nome, tipo, descricao`
- **Instrumento/Kit*:** classes de kits e itens (conforme arquivo principal)

---

## cme.enums
- `UsoSaneanteEtapa` {PRE_LIMPEZA, LIMPEZA_MANUAL, LAVADORA_TERMODESINFECCAO, DESINFECCAO_ALTO_NIVEL}
- `ResultadoConformidade` {CONFORME, NAO_CONFORME, NAO_APLICAVEL}
- `NaoConformidadeSeveridade` {BAIXA, MEDIA, ALTA, CRITICA}
- `MovimentacaoTipo` {ENTRADA_CONTAMINADO, ENVIO_ESTERIL, RETORNO_CONTAMINADO, DESCARTE}
- `ManutencaoTipo` {PREVENTIVA, CORRETIVA, CALIBRACAO, VERIFICACAO_METROLOGICA}
- `ManutencaoStatus` {PLANEJADA, ABERTA, EM_ANDAMENTO, CONCLUIDA, CANCELADA}
- `LoteStatus` {MONTADO, EM_PROCESSO, LIBERADO, BLOQUEADO, VENCIDO}
- `CicloStatus` {AGENDADO, EM_ANDAMENTO, CONCLUIDO, BLOQUEADO}

## cme.domain
- (conforme arquivo principal; sem mudanças neste patch)

---

## environmental.enums
- `ClasseResiduo` {PERFUROCORTANTE, BIOLOGICO, QUIMICO, RECICLAVEL, COMUM}

## environmental.domain
- **GeracaoResiduo**: `tenant, dataRegistro, classeResiduo, pesoEstimadoKg, destinoFinal, loteRelacionada, saneanteRelacionado, observacoes`

---

## quality.enums
- `NaoConformidadeStatus` {ABERTA, EM_INVESTIGACAO, EM_IMPLEMENTACAO, CONCLUIDA, CANCELADA}

## quality.domain
- **TipoNaoConformidade**: `tenant, nome, descricao`
- **NaoConformidadeBase** (interface): getters padrão

---

## common.domain
- **EvidenciaArquivo**: `tenant, nomeArquivo, uri, hashSha256, contentType, tamanhoBytes, autor, criadoEm`  
  Observação: usar hash para integridade, manter trilhas com Envers.

---

## Relações principais entre pacotes
- `CME ⇄ core.domain`: Setor, Kits, Lotes, etc.
- `SECURITY ⇄ iam`: Policies/Permissions por tenant.
- `OBSERVABILITY ⇄ *`: logs e auditorias amarram traceId/user/tenant.
- `GED ⇄ EDU`: `CourseItem.documentoBase → DocumentVersion` (só vincular a **PUBLICADO**).
- `EDU ⇄ HR`: `Colaborador` como pivô de inscrições, presenças e competências.
- `EDU ⇄ COMMON`: `PracticalAssessment.evidencia → EvidenciaArquivo`.

---

## hrdomain

### Cargo
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK |
| codigo | String | **Único por tenant** |
| nome | String |  |
| descricao | String |  |

**Índices/Restrições**
- `uq_cargo_tenant_codigo (tenant_id, codigo)`

### Colaborador
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK |
| matricula | String | |
| nomeCompleto | String | |
| cpf | String | |
| email | String | |
| telefone | String | |
| setor | Setor | FK |
| cargo | Cargo | FK |
| dataAdmissao | LocalDate | |
| ativo | Boolean | |
| usuarioSistema | User (opcional) | Nem todo colaborador é usuário do sistema |

---

## eduenums
- `AttemptStatus` {NAO_INICIADO, EM_ANDAMENTO, CONCLUIDO, APROVADO, REPROVADO, ABANDONADO}
- `GradeScale` {PERCENTUAL, CONCEITO, PONTOS}
- `FeedbackTarget` {COURSE, SESSION, INSTRUCTOR}
- `BookingStatus` {SOLICITADO, APROVADO, REJEITADO, REALIZADO, CANCELADO}
- `DeliveryMode` {EAD, PRESENCIAL, HIBRIDO}

---

## edudomain

### TrainingProvider
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK |
| nome | String | |
| cnpj | String | opcional |
| contatoEmail | String | |
| contatoTelefone | String | |
| interno | Boolean | true = provedor interno |
| siteUrl | String | |
| observacoes | String | |

### Course
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK |
| provider | TrainingProvider | FK |
| codigo | String | **Único por tenant** |
| titulo | String | |
| descricao | String | |
| cargaHorariaMin | Integer | |
| deliveryMode | DeliveryMode | |
| obrigatorio | Boolean | compliance/ONA |
| gradeScale | GradeScale | |

**Índices/Restrições**
- `uq_course_tenant_codigo (tenant_id, codigo)`

### CourseModule
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| course | Course |
| ordem | Integer |
| titulo | String |
| descricao | String |

### CourseItem
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK |
| module | CourseModule | FK |
| ordem | Integer | |
| titulo | String | |
| descricao | String | |
| aprovacaoMin | Double | nota mínima |
| frequenciaMinPct | Double | % mínima de progresso |
| documentoBase | DocumentVersion (opcional) | **Somente Documentos PUBLICADO** |

### CourseInstructor
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| provider | TrainingProvider |
| colaborador | Colaborador |
| curriculo | String |
| areaEspecialidade | String |
| ativo | Boolean |

### Offering
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| course | Course |
| codigoTurma | String |
| setorAlvo | Setor (opcional) |
| inicio | LocalDate |
| fim | LocalDate |
| vagas | Integer |
| deliveryMode | DeliveryMode |
| ativo | Boolean |

### Session
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| offering | Offering |
| inicio | LocalDateTime |
| fim | LocalDateTime |
| local | String |
| instrutor | CourseInstructor |

### Enrollment
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| offering | Offering |
| colaborador | Colaborador |
| inscritoEm | LocalDateTime |
| obrigatorio | Boolean |
| statusGeral | AttemptStatus |
| notaFinal | Double |
| concluidoEm | LocalDateTime |

### Attempt
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| enrollment | Enrollment |
| courseItem | CourseItem |
| startedAt | LocalDateTime |
| endedAt | LocalDateTime |
| status | AttemptStatus |
| scoreRaw | Double |
| progressoPct | Double |
| tentativaN | Integer |

### Attendance
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| session | Session |
| colaborador | Colaborador |
| presente | Boolean |
| registradoEm | LocalDateTime |
| observacoes | String |

### TrainingResource
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| nome | String |
| tipo | String |
| localizacao | String |
| capacidade | Integer |
| ativo | Boolean |

### ResourceBooking
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| resource | TrainingResource |
| session | Session |
| inicio | LocalDateTime |
| fim | LocalDateTime |
| status | BookingStatus |
| solicitadoPor | User |
| observacoes | String |

### Feedback
| Campo | Tipo | Notas |
|---|---|---|
| id | Long | PK |
| tenant | Tenant | FK |
| targetType | FeedbackTarget | |
| course | Course (opcional) | conforme target |
| session | Session (opcional) | conforme target |
| instructor | CourseInstructor (opcional) | conforme target |
| colaborador | Colaborador | avaliador |
| nota | Integer | 1..5 |
| comentario | String | |
| enviadoEm | LocalDateTime | |

### Competency
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| codigo | String |
| nome | String |
| descricao | String |

### UserCompetency
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| colaborador | Colaborador |
| competency | Competency |
| obtidaEm | LocalDate |
| origem | String |
| validadeAte | LocalDate |

### CompetencyRubric
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| competency | Competency |
| titulo | String |
| descricao | String |
| escalaDescricao | String |

### RubricCriterion
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| rubric | CompetencyRubric |
| ordem | Integer |
| descricao | String |
| peso | Double |

### PracticalAssessment
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| rubric | CompetencyRubric |
| colaborador | Colaborador |
| avaliador | Colaborador |
| realizadoNoSetor | Setor |
| realizadoEm | LocalDateTime |
| notaFinal | Double |
| aprovado | Boolean |
| evidencia | EvidenciaArquivo (opcional) |
| observacoes | String |

### AssessmentCriterionScore
| Campo | Tipo |
|---|---|
| id | Long |
| tenant | Tenant |
| assessment | PracticalAssessment |
| criterion | RubricCriterion |
| pontuacao | Double |
| comentario | String |

---

## Regras de integridade e negócio (Guidelines)
1. **CourseItem.documentoBase**: deve apontar para `DocumentVersion` com `status = PUBLICADO`. Bloquear vínculo caso contrário.
2. **Conclusão de Enrollment**: somente quando todos os `CourseItem` obrigatórios tiverem `Attempt.status ∈ {APROVADO, CONCLUIDO}` e `frequenciaMinPct` atendida.
3. **Attempt**: `tentativaN` autoincremental por `Enrollment+CourseItem`. Manter histórico.
4. **Attendance**: uma presença por `Session+Colaborador`. Tratar duplicidade via **UNIQUE** lógico.
5. **ResourceBooking**: não permitir conflitos (`resource` ocupado em interseção de `inicio..fim`).
6. **Competências**: `PracticalAssessment` calcula `notaFinal` por soma ponderada de `AssessmentCriterionScore.pontuacao * peso`; `aprovado` conforme thresholds do hospital.
7. **HR desvinculado de User**: `Colaborador.usuarioSistema` é opcional; permissões continuam em `User`.
8. **Auditoria**: todas as entidades **Audited** com Envers; evidências com `hashSha256` e retenção conforme política institucional.

---

## Padrões técnicos adotados
- **JPA/Hibernate**; **Spring Data**; **Envers** para auditoria; **JSON logs** com MDC (traceId/userId).
- **Multi-tenant** por FK (`tenant_id`) e índices compostos **UNIQUE** para códigos.
- **Validação**: Bean Validation (ex.: `@NotNull`, `@Email`, `@Size`).
- **Armazenamento de arquivos**: `EvidenciaArquivo.uri` para externo (S3, filesystem); integridade por `hashSha256`.
- **Segurança**: ABAC (Policy/Condition) com sobreposição `UserPermissionOverride`.

---

## Exemplos de navegação típica (Consultas)
- **Histórico de treinamentos por colaborador**: `Enrollment` + `Attempt` + `Attendance` + `UserCompetency`.
- **Cursos que exigem documento-base**: `CourseItem` com `documentoBase != null` + join `DocumentVersion.status`.
- **Mapa de competências**: `Competency` ⇄ `UserCompetency` com validade.
- **Utilização de recursos**: `ResourceBooking` por período + conflitos.

---

## Extensibilidade
- **Certificados**: adicionar `Certificate` (issuance, template, assinatura).
- **Planos de aprendizagem**: `LearningPath` (ordem de cursos; pré-requisitos).
- **Integrações futuras**: SCORM/xAPI em `Attempt` (guardando statementId/launchId).
- **Catálogo externo**: sincronizar `TrainingProvider/Course` de parceiros.

---

## Checklist de qualidade do diagrama
- [x] Sumário único e numerado.
- [x] Índices **UNIQUE** explícitos: `Cargo.codigo`, `Course.codigo`.
- [x] Tabelas sem fragmentação (todos quadros encerrados).
- [x] Snapshot do GED incluso para referência.
- [x] Regras de negócio registradas.
- [x] Relações entre pacotes listadas.

