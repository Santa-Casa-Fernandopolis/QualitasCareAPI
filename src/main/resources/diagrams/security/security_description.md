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
* **Atributos-chave**: identificação, vínculo com **Tenant** ativo, metadados (setor, cargo, etc.).
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

---

# Camada de aplicação (serviços de autorização)

## AuthContext

* **O que é**: *snapshot* do usuário autenticado.
* **Conteúdo**: `userId`, `tenantId`, `roles` (nomes), metadados (ex.: setor), etc.
* **Função**: fornece o contexto para decisões de acesso.

## HospitalPermissionEvaluator (PermissionEvaluator do Spring)

* **O que é**: ponto de checagem usado em `@PreAuthorize`.
* **Função**:

    1. **Interpretar a permissão declarativa** (`"NC:READ@DETALHE"`).
    2. Invocar o **AccessDecisionService**, passando `AuthContext`, `resource`, `action`, `feature`, e o **alvo** (quando houver ID de entidade).
* **Robustez**: valida formato e enums; mensagens claras ao negar acesso.

## AccessDecisionService

* **O que é**: orquestrador da decisão.
* **Fluxo de decisão** (nesta ordem):

    1. **UserPermissionOverride** (exato e fallback de feature) ⇒ se existir, **vence tudo** (DENY > ALLOW).
    2. **Policies** (por prioridade ascendente): avalia condições; **DENY** interrompe e nega, **ALLOW** concede se não houver DENY anterior aplicável.
    3. **RBAC (Role → Permission)**: verifica existência de permissão por role (match exato + fallback de feature).
    4. **Fail-safe**: ausência de match ⇒ **nega**.
* **Observações**:

    * Avaliação **multi-tenant** (sempre filtra por `tenantId`).
    * **Fallback de feature**: específica → curinga (feature ausente).
    * Pode aplicar **cache** leve (por usuário/tenant e escopo `(resource, action, feature)`).

## CurrentUserExtractor

* **O que é**: extrai `AuthContext` do `Authentication` (Spring Security).
* **Função**: normaliza *claims* do token/session, determinando `tenantId`, roles, setor etc.

## TargetLoader

* **O que é**: carrega o **alvo** (entidade) quando a autorização depende de atributos do registro (owner, setor, status).
* **Função**: fornecer objeto para o avaliador ABAC com o menor custo (pode usar projeções ou cache).

## PolicyEvaluator

* **O que é**: executa as **PolicyCondition** sobre `(AuthContext, alvo, contexto)`.
* **Função**: resolver operadores (`EQ`, `IN`, `NE`, etc.) e tipos (`TARGET_DEPARTMENT`, `USER_PROFESSION`, `TARGET_STATUS`…), preferencialmente via *registry*/enum de condições suportadas.

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
