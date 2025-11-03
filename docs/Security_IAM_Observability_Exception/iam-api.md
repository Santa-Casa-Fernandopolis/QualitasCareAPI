# API de Identity & Access Management (IAM)

Os recursos IAM expõem operações administrativas sobre locatários e identidades. Todos os endpoints exigem autenticação com o papel `SYSTEM_ADMIN`.

## Convenções gerais
- **Formato:** JSON em requisições e respostas.
- **Paginação:** consultas retornam `Page<T>` e aceitam `page`, `size` e `sort`.
- **Enumerações relevantes:**
  - `UserStatus`: `PROVISIONED`, `ACTIVE`, `SUSPENDED`, `DISABLED`, `EXPIRED`.
  - `IdentityOrigin`: `LOCAL`, `LDAP`, `SSO`, `IMPORTED`.

## Locatários (`/api/tenants`)
| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET` | `/api/tenants` | Lista paginada de locatários configurados. |
| `GET` | `/api/tenants/{id}` | Detalha um locatário específico. |
| `POST` | `/api/tenants` | Cria um novo locatário. |
| `PUT` | `/api/tenants/{id}` | Atualiza dados de um locatário. |
| `DELETE` | `/api/tenants/{id}` | Exclui um locatário. |

### Estruturas
- **TenantDto**: `{ id, code, name, active }`
- **TenantRequest** (POST/PUT): `{ code*, name*, active }`

## Usuários (`/api/users`)
| Método | Caminho | Descrição |
| --- | --- | --- |
| `GET` | `/api/users` | Lista paginada de usuários. |
| `GET` | `/api/users/{id}` | Obtém detalhes completos de um usuário. |
| `POST` | `/api/users` | Cria uma nova identidade de usuário. |
| `PUT` | `/api/users/{id}` | Atualiza os dados de um usuário existente. |
| `DELETE` | `/api/users/{id}` | Remove um usuário. |

### Estruturas
- **UserDto**: `{ id, username, fullName, department, status, origin, createdAt, activatedAt, expiresAt, updatedAt, tenantId, tenantCode, roles[] }`, onde `roles[]` contém `{ id, name, description }`.
- **UserCreateRequest** (POST): `{ username*, password* (mínimo 6 caracteres), fullName, department, tenantId*, status, origin, activatedAt, expiresAt, roleIds[] }`
- **UserUpdateRequest** (PUT): `{ fullName, department, status, origin, activatedAt, expiresAt, password (mínimo 6 caracteres), roleIds[] }`

### Considerações adicionais
- Senhas são exigidas apenas na criação ou quando explicitamente fornecidas na atualização.
- Campos marcados com `*` são obrigatórios.
- Datas utilizam `ISO-8601` (`yyyy-MM-dd'T'HH:mm:ss`).

Consulte a documentação de tratamento de exceções para entender os formatos de erro retornados em violações de validação ou regras de negócio.
