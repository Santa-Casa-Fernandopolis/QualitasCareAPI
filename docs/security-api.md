# API de Segurança

Esta documentação descreve os recursos expostos sob `/api` relacionados a autorização avançada. Todos os endpoints descritos aqui exigem que o chamador esteja autenticado e possua o papel `SYSTEM_ADMIN`, conforme aplicado pelas anotações `@PreAuthorize` dos controladores.

## Convenções gerais
- **Formato de dados:** todas as requisições e respostas utilizam JSON.
- **Paginação:** endpoints `GET` que retornam coleções aceitam os parâmetros `page`, `size` e `sort` do Spring Data (`page` inicia em 0).
- **Enumerações:**
  - `Action`: `READ`, `CREATE`, `UPDATE`, `DELETE`, `APPROVE`, `EXPORT`, `CLOSE`.
  - `ResourceType`: `INDICADOR`, `AUDITORIA`, `NC`, `PROTOCOLO`, `CAPACITACAO`, `PGRSS`, `USUARIO`, `DASHBOARD`.
  - `Effect`: `ALLOW`, `DENY`.

## Perfis (`/api/roles`)
| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET` | `/api/roles` | Lista paginada de perfis. |
| `GET` | `/api/roles/{id}` | Obtém um perfil pelo identificador. |
| `POST` | `/api/roles` | Cria um novo perfil. |
| `PUT` | `/api/roles/{id}` | Atualiza um perfil existente. |
| `DELETE` | `/api/roles/{id}` | Remove um perfil. |

### Estruturas
- **RoleDto**: `{ id, name, description, tenantId }`
- **RoleRequest** (POST/PUT): `{ name*, description, tenantId* }`

## Permissões (`/api/permissions`)
| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET` | `/api/permissions` | Lista paginada de permissões atômicas. |
| `GET` | `/api/permissions/{id}` | Obtém uma permissão pelo identificador. |
| `POST` | `/api/permissions` | Cria uma permissão. |
| `PUT` | `/api/permissions/{id}` | Atualiza uma permissão existente. |
| `DELETE` | `/api/permissions/{id}` | Remove uma permissão. |

### Estruturas
- **PermissionDto**: `{ id, resource, action, feature, code, tenantId }`
- **PermissionRequest** (POST/PUT): `{ resource*, action*, feature, code, tenantId* }`

## Políticas (`/api/policies`)
| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET` | `/api/policies` | Lista paginada de políticas de autorização. |
| `GET` | `/api/policies/{id}` | Obtém uma política específica. |
| `POST` | `/api/policies` | Cria uma nova política. |
| `PUT` | `/api/policies/{id}` | Atualiza uma política existente. |
| `DELETE` | `/api/policies/{id}` | Remove uma política. |

### Estruturas
- **PolicyDto**: `{ id, tenantId, resource, action, feature, effect, enabled, priority, description, roles[], conditions[] }`, em que:
  - `roles[]` é uma lista de `{ id, name }`.
  - `conditions[]` é uma lista de `{ id, type, operator, value }`.
- **PolicyRequest** (POST/PUT): `{ tenantId*, resource*, action*, feature, effect*, enabled, priority, description, roleIds[], conditions[] }`, com `conditions[]` composto por `{ type*, operator*, value* }`.

## Associação de perfis e permissões (`/api/role-permissions`)
| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET` | `/api/role-permissions` | Lista paginada de vínculos entre perfis e permissões. |
| `GET` | `/api/role-permissions/{id}` | Obtém um vínculo específico. |
| `POST` | `/api/role-permissions` | Cria um vínculo entre perfil e permissão. |
| `PUT` | `/api/role-permissions/{id}` | Atualiza um vínculo existente. |
| `DELETE` | `/api/role-permissions/{id}` | Remove um vínculo. |

### Estruturas
- **RolePermissionDto**: `{ id, tenantId, roleId, permissionId }`
- **RolePermissionRequest** (POST/PUT): `{ tenantId*, roleId*, permissionId* }`

## Overrides de permissão por usuário (`/api/user-overrides`)
| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET` | `/api/user-overrides` | Lista paginada de exceções de permissão aplicadas a usuários. |
| `GET` | `/api/user-overrides/{id}` | Obtém uma exceção específica. |
| `POST` | `/api/user-overrides` | Cria uma exceção de permissão. |
| `PUT` | `/api/user-overrides/{id}` | Atualiza uma exceção de permissão existente. |
| `DELETE` | `/api/user-overrides/{id}` | Remove uma exceção de permissão. |

### Estruturas
- **UserPermissionOverrideDto**: `{ id, tenantId, userId, resource, action, feature, effect, priority, reason, validFrom, validUntil, approved, dualApprovalRequired, requestedBy, approvedBy, approvedAt }`
- **UserPermissionOverrideRequest** (POST/PUT): `{ tenantId*, userId*, resource*, action*, feature, effect*, priority, reason, validFrom, validUntil, approved, dualApprovalRequired, requestedBy, approvedBy, approvedAt }`

### Observações sobre validação
Os campos marcados com `*` são obrigatórios conforme as anotações de validação. Em caso de violação, o serviço responde com `400 Bad Request` contendo a estrutura de erro padrão descrita na documentação de tratamento de exceções.
