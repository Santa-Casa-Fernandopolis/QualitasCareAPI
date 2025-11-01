# AccessDecisionServiceIntegrationTest

Documentação dos cenários de teste implementados em `com.erp.qualitascareapi.security.app.AccessDecisionServiceIntegrationTest`.

## Contexto Geral
- **Tipo de teste:** Integração com Spring Boot (`@SpringBootTest`, perfil `test`).
- **Componentes envolvidos:**
  - `AccessDecisionService`
  - `UserRepository`
  - `UserPermissionOverrideRepository`
  - `PolicyRepository`
- **Objetivo:** Garantir que a decisão de acesso considere políticas, papéis, overrides e dados do alvo de forma integrada.

## Preparação Comum
- Usuários de referência (`enf.scf` e `admin.scf`) são carregados do banco de dados de teste antes de cada cenário.
- Helper `authContextFrom` converte a entidade `User` em `AuthContext` com roles e metadados necessários.

## Casos de Teste

### nurseCanReadListWithinOwnDepartmentThroughPolicy
- **Cenário:** Enfermeiro acessa leitura de lista do mesmo departamento.
- **Verificação:** Serviço retorna `true`, confirmando política existente para leitura.

### nurseCannotReadListFromDifferentDepartment
- **Cenário:** Enfermeiro tenta ler lista de outro departamento.
- **Verificação:** Serviço retorna `false`, demonstrando que política restringe o escopo por departamento.

### adminShouldUpdateNcViaPolicy
- **Cenário:** Administrador solicita atualização de NC sem alvo específico.
- **Verificação:** Serviço retorna `true`, validando política de atualização para administradores.

### adminReceivesRbacGrantForCreateAction
- **Cenário:** Administrador solicita criação de NC.
- **Verificação:** Serviço retorna `true`, indicando permissão concedida via RBAC/política.

### nurseShouldBeDeniedNcUpdateWithoutOverride
- **Cenário:** Enfermeiro tenta atualizar NC sem override.
- **Verificação:** Serviço retorna `false`, mostrando que política padrão não permite a operação.

### nurseOverrideShouldGrantTemporaryUpdateAccess
- **Cenário:** Override temporário de atualização é criado para o enfermeiro.
- **Verificações:**
  - Override é persistido com validade atual.
  - Serviço retorna `true`, evidenciando que overrides aprovados concedem acesso.

### explicitDenyOverrideTakesPrecedenceOverPolicyAllow
- **Cenário:** Override explícito de negação para leitura de lista.
- **Verificações:**
  - Override `Effect.DENY` é salvo com prioridade alta.
  - Serviço retorna `false`, confirmando precedência de negações explícitas sobre políticas permissivas.

### policyConditionShouldAllowUpdateWhenTargetMatchesContext
- **Cenário:** Política adicional permite atualização quando departamento do alvo coincide com o contexto.
- **Preparação:** Política e condição são persistidas para o tenant do enfermeiro.
- **Verificação:** Serviço retorna `true` ao avaliar target que expõe `getDepartment()` compatível.

## Dados de Apoio
- Classe interna `NcProjection` representa um alvo resumido utilizado na verificação de leitura.
- `NcRecord` (record interno) expõe `getDepartment()` para satisfazer condições baseadas em departamento.
