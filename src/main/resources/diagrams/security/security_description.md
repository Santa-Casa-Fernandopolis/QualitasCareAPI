# Visão geral

O sistema implementa **controle de acesso híbrido** (RBAC + ABAC), com:

* **RBAC**: papéis (roles) concedem permissões básicas por **módulo** e **ação**, com granularidade por **feature** (tela/funcionalidade).
* **ABAC** (opcional): *policies* condicionais (por exemplo, setor do usuário = setor do registro).
* **Overrides por usuário**: exceções específicas (ALLOW/DENY) que prevalecem sobre o papel.
* **Multi-tenant**: todos os artefatos (dados e segurança) particionados por **Tenant** (hospital/unidade).

A permissão alvo é endereçada como `MODULO:ACAO@FEATURE`
Ex.: `NC:READ@DETALHE`, `INDICADOR:EXPORT@RELATORIO`, `PROTOCOLO:READ@*`.

---

# Camada de domínio (dados)

## Tenant

* **O que é**: contexto organizacional (hospital/unidade).
* **Papel**: isola dados e regras de acesso por cliente.
* **Relações**: referenciado por *User*, *Permission*, *Policy*, *RolePermission*, *UserPermissionOverride* e pelas entidades de negócio (NC, Auditoria, Indicadores…).

## User

* **O que é**: pessoa autenticada no sistema.
* **Atributos-chave**: identificação, vínculo com **Tenant** ativo, metadados (setor, profissão), status e origem da identidade.
* **Relações**: associação com um conjunto de **Roles** dentro do *Tenant*.

## Role

* **O que é**: agrupador de permissões (ex.: ENFERMEIRO, TECNICO, MEDICO, ADMIN_QUALIDADE).
* **Relações**:

    * *RolePermission*: define o escopo RBAC por *Tenant*.
    * *User–Role*: usuários herdam permissões do(s) seu(s) papel(eis).

## Permission

* **O que é**: escopo RBAC elementar.
* **Campos**:

    * `resource` (**ResourceType**): **módulo** (ex.: NC, AUDITORIA, INDICADOR…).
    * `action` (**Action**): **operação** (READ, CREATE, UPDATE, DELETE, APPROVE, EXPORT, CLOSE).
    * `feature` (String, opcional): sub-tela/funcionalidade (LISTA, DETALHE, FORM, RELATORIO, DASH…). Ausente ⇒ curinga (módulo inteiro).
    * `tenant` (**Tenant**): escopo organizacional.
    * (Opcional) `code`: identificador humano (“NC_READ@LISTA”).
* **Função**: expressa “o que pode ser feito, onde e, opcionalmente, em qual tela”.

## RolePermission

* **O que é**: vínculo **Role ↔ Permission** por *Tenant*.
* **Função**: materializa o RBAC — quais permissões cada papel possui naquele hospital.

## UserPermissionOverride

* **O que é**: exceção individual (ALLOW/DENY) para um usuário, por *Tenant*.
* **Campos-chave**: `effect`, `priority`, janelas `validFrom/validUntil`, flags `approved`/`dualApprovalRequired`, metadados (`reason`, `requestedBy`, `approvedBy`, `approvedAt`).
* **Função**: granularidade fina para ajustes de acesso específicos, **precedendo** RBAC e *policies*.

## Policy

* **O que é**: regra ABAC opcional e ordenada por prioridade.
* **Campos**:

    * `resource`, `action`, `feature` (mesma semântica das *permissions*, `feature` opcional).
    * `effect` (**Effect**): ALLOW ou DENY.
    * `enabled` (bool), `priority` (int, menor = mais alta).
    * `roles` (conjunto opcional): escopo de papéis aos quais a policy se aplica.
    * `tenant` (**Tenant**).
    * `conditions` (lista de **PolicyCondition**).
* **Função**: impor **condições de contexto** (ex.: “TÉCNICO só lê DETALHE de NC do próprio setor”).
  **Precedência**: DENY vence ALLOW; prioridade ordena avaliação.

## PolicyCondition

* **O que é**: predicado avaliado sobre **usuário**, **alvo** e **contexto** (ex.: `TARGET_DEPARTMENT EQ CURRENT_DEPT`).
* **Função**: compor regras ABAC. Tipos usuais:

    * **Usuário**: setor, profissão, plantão, ID.
    * **Alvo** (entidade carregada): setor do registro, proprietário, status.
    * **Contexto**: turno, unidade, flags organizacionais.

---

# Enums e vocabulário controlado

## ResourceType (Módulo)

* **Exemplos**: INDICADOR, AUDITORIA, NC, PROTOCOLO, CAPACITACAO, PGRSS, USUARIO, DASHBOARD.
* **Função**: centraliza os “grandes blocos” do sistema, com **feature** refinando a tela.

## Action (Operação)

* **Exemplos**: READ, CREATE, UPDATE, DELETE, APPROVE, EXPORT, CLOSE.
* **Função**: padroniza verbos de negócio, evitando proliferação de ações ad-hoc.

## Effect

* **Valores**: ALLOW, DENY.
* **Função**: efeito final de *policy* ou *override*.

## Feature (vocabulário)

* **Padrão**: `LISTA`, `DETALHE`, `FORM`, `RELATORIO`, `DASH`, `PLANEJAMENTO`, `EXECUCAO`, `FECHAMENTO`.
* **Função**: granularidade de UI/fluxo sem explodir enums (string controlada).

## UserStatus

* **Valores**: PROVISIONED, ACTIVE, SUSPENDED, DISABLED, EXPIRED.
* **Função**: determina se o usuário está elegível (`isActive()` true) para autenticação/autorização.

## IdentityOrigin

* **Valores**: LOCAL, LDAP, SSO, IMPORTED.
* **Função**: rastreia a fonte da identidade (auditoria e decisões condicionais).

---

# Camada de autenticação (Spring Authorization Server)

## Authorization Server embarcado

* **Papel**: emite tokens OAuth2/JWT para usuários e integrações usando os fluxos `authorization_code` (com PKCE) e `client_credentials`.
* **Localização**: pacote `authserver/` com `AuthorizationServerConfig`, provedores de cliente (`RegisteredClientRepository`) e persistência de autorizações.
* **Integração IAM**: delega autenticação ao `LocalUserDetailsService` do módulo `iam/`, que resolve credenciais, status e `tenant` ativo.

## Customização de JWT

* **Claims**: adiciona `tenant_id`, `user_id`, `department`, `profession`, `user_status`, `origin`, `roles` normalizados e `tenant_authorities` (`TENANT_{id}`) para consumo pelo `CurrentUserExtractor`.
* **Chaves**: tokens são assinados com par RSA gerenciado pelo SAS (`JWKSource` local) e expõem a chave pública via `/.well-known/jwks.json`.
* **Expiração**: access tokens curtos (15 min recomendados) e refresh tokens opcionais, com política de revogação em banco (`OAuth2Authorization`).

## Fluxo de login

1. O front-end redireciona o usuário para `/oauth2/authorize` informando `tenant` escolhido.
2. SAS autentica credenciais via `LocalUserDetailsService`, que valida status ativo, vínculos de tenant e carrega `roles`/atributos.
3. `JwtCustomizer` injeta as claims multi-tenant e normaliza authorities.
4. O token JWT é retornado ao cliente, que o envia na API principal (`Authorization: Bearer ...`).
5. A API, configurada como **Resource Server**, valida assinatura e alimenta `AuthContext` via `CurrentUserExtractor`.

![Fluxo de autenticação](security_auth_sequence_diagram.puml)

---

# Camada de aplicação (serviços de autorização)

## SecurityConfig (Resource Server)

* **Papel**: registra o `SecurityFilterChain` que habilita `oauth2ResourceServer().jwt()` e aplica o `JwtAuthenticationConverter` customizado com authorities `ROLE_*` e `TENANT_*`.
* **Integração**: injeta o `CurrentUserExtractor` e `AccessDecisionService` para que endpoints protegidos (`@PreAuthorize`) reutilizem o mesmo contexto RBAC+ABAC.
* **Fallbacks**: política `deny-all` por padrão, liberando apenas endpoints públicos (`/actuator/health`, `/oauth2/**`, `/login` do SAS).

## AuthContext

* **O que é**: *snapshot* do usuário autenticado.
* **Conteúdo**: `userId`, `username`, `tenantId`, `roles` normalizados, `department`, `profession`, `status`, `origin`, mapa de `attributes` e utilitários como `isActiveUser()`.
* **Função**: fornece o contexto completo para decisões de acesso, inclusive tokens dinâmicos para ABAC.

## HospitalPermissionEvaluator (PermissionEvaluator do Spring)

* **O que é**: ponto de checagem usado em `@PreAuthorize`.
* **Função**:

    1. **Interpretar a permissão declarativa** (`"NC:READ@DETALHE"`).
    2. Invocar o **AccessDecisionService**, passando `AuthContext`, `resource`, `action`, `feature`, e o **alvo** (quando houver ID de entidade).
* **Robustez**: valida formato e enums; mensagens claras ao negar acesso.

## AccessDecisionService

* **O que é**: orquestrador da decisão.
* **Fluxo de decisão** (nesta ordem):

    0. **Guarda inicial**: exige usuário ativo (`UserStatus.isActive()`) e `tenantId` presente — caso contrário, nega e audita motivo.
    1. **UserPermissionOverride** (exato e fallback de feature) ⇒ se existir, **vence tudo** (DENY > ALLOW) respeitando janelas `validFrom/validUntil` e flag de aprovação.
    2. **Policies** (por prioridade ascendente): avalia filtros de role e condições; **DENY** interrompe e nega, **ALLOW** concede se não houver DENY anterior aplicável.
    3. **RBAC (Role → Permission)**: verifica existência de permissão por role (match exato + fallback de feature).
    4. **Fail-safe**: ausência de match ⇒ **nega**.
* **Observações**:

    * Avaliação **multi-tenant** (sempre filtra por `tenantId`).
    * **Fallback de feature**: específica → curinga (feature ausente).
    * Gera **logs estruturados** (`stage`, `effect`, detalhes) para auditoria.

## CurrentUserExtractor

* **O que é**: extrai `AuthContext` do `Authentication` (Spring Security).
* **Função**: lê tokens `Jwt` (claims `user_id`, `sub`, `tenant_id`, `department`, `profession`, `user_status`, `origin`, mapas `attributes`/`clinical_attributes`), converte para tipos fortes e normaliza roles (`ROLE_`, maiúsculas). Na ausência de `tenant_id`, tenta inferir via autoridade `TENANT_{id}`. Fallback para `UserDetails` grava metadados mínimos. Garante defaults seguros (`status=ACTIVE`, `origin=LOCAL`).

## TargetLoader

* **O que é**: carrega o **alvo** (entidade) quando a autorização depende de atributos do registro (owner, setor, status).
* **Função**: fornecer objeto para o avaliador ABAC com o menor custo (pode usar projeções ou cache). A implementação padrão é um *stub* que retorna `null`; os módulos devem sobrepor com buscas específicas.

## PolicyEvaluator

* **O que é**: executa as **PolicyCondition** sobre `(AuthContext, alvo, contexto)`.
* **Função**: resolver operadores (`EQ`, `IN`, `NOT_IN`, `CONTAINS_*`, `BETWEEN`, `BEFORE`, `AFTER`), tokens dinâmicos (`CURRENT_DEPT`, `CURRENT_TENANT`, `CURRENT_USER_ID`, `CURRENT_PROFESSION`) e extrair atributos do alvo por reflexão (`department`, `status`, `ownerId`, `tenantId`, `tags`). Condições desconhecidas retornam `false` (fail-safe).

---

# Camada de persistência (repositórios)

## PermissionRepository / PolicyRepository / RolePermissionRepository / UserPermissionOverrideRepository

* **Papel**: fornecer consultas que **combinem match exato e fallback de feature** em **uma única ida ao banco** (ex.: `feature = :f OR feature IS NULL`), sempre filtrando por **tenant**.
* **Boas práticas**: evitar `EAGER` em coleções; usar *join fetch* controlado; índices em `(tenant, resource, action, feature)` e `(tenant, code)`.

---

# Regras de precedência e resolução

1. **Override por usuário**: **mais forte** (DENY > ALLOW).
2. **Policies (ABAC)**: ordenadas por `priority` (menor primeiro).

    * Encontrou **DENY aplicável** ⇒ **nega** imediatamente.
    * Encontrou **ALLOW aplicável** ⇒ **permite** (desde que não haja DENY mais prioritário que também se aplique).
3. **RBAC (Role → Permission)**: se role do usuário possui permissão (match exato ou fallback), **permite**.
4. **Fail-safe**: sem match ⇒ **nega**.

---

# Multi-tenant (como aparece em segurança)

* **Todos** os artefatos de segurança e dados de negócio são **escopados por Tenant**.
* É possível ter **mesmo Role com políticas diferentes** por hospital (ex.: ENFERMEIRO no Hospital A pode criar NC; no Hospital B, não).
* O `tenantId` é definido no login (ou via subdomínio/cabeçalho) e aplicado **transversalmente** nas consultas.

---

# Exemplos de uso (cenários)

* **`NC:READ@DETALHE` (TÉCNICO)**
  *Policy* ALLOW: só se `TARGET_DEPARTMENT == CURRENT_DEPT`.
  *Policy* DENY (prioridade maior): nega se `TARGET_DEPARTMENT != CURRENT_DEPT`.
  Papel TÉCNICO pode ler DETALHE, mas **apenas** do próprio setor (ABAC).

* **`INDICADOR:EXPORT@RELATORIO` (ADMIN_QUALIDADE)**
  Sem *policy* para outros papéis, apenas Role→Permission para ADMIN_QUALIDADE.
  Técnicos e enfermeiros, mesmo com READ, **não exportam**.

* **Fallback por feature**
  Usuário não tem `NC:READ@DETALHE`, mas o papel possui `NC:READ@*` ⇒ leitura permitida no módulo inteiro.

---

# Invariantes e boas práticas

* **Consistência de feature**: vocabulário controlado (maiúsculas, `A_Z0_9_`).
* **Índices por tenant**: `(tenant, resource, action, feature)` garantem unicidade e performance.
* **`feature` opcional como curinga**: ausente ⇒ “todas as features do módulo”.
* **DENY prevalece**; **fail-safe**: na dúvida, negar.
* **Testes**: precedência (override > policy > role), fallback, multi-tenant e cenários com alvo (owner/setor/status).

---
