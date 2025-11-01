# PolicyEvaluatorTest

Documentação dos cenários de teste implementados em `com.erp.qualitascareapi.security.app.PolicyEvaluatorTest`.

## Contexto Geral
- **Classe sob teste:** `PolicyEvaluator`
- **Objetivo:** Validar o mecanismo de avaliação de políticas de autorização considerando diferentes tipos de condição, operadores e dados dinâmicos.

## Casos de Teste

### matchesAll_returnsTrueWhenEveryConditionMatches
- **Cenário:** Política composta por múltiplas condições de departamento, tags, proprietário, tenant e atributos do usuário.
- **Preparação:** Contexto de autenticação com usuário ativo local, role "ENFERMEIRO" e atributo `shift=DAY`; alvo com departamento UTI, tags "critical" e "uti".
- **Verificações:**
  - `matchesAll` retorna `true` quando todas as condições são satisfeitas simultaneamente.

### matchesAll_returnsFalseWhenAnyConditionFails
- **Cenário:** Política que exige ausência de tag sensível e status de usuário ativo.
- **Preparação:** Alvo contendo tag proibida `"sensivel"`.
- **Verificações:**
  - `matchesAll` retorna `false` ao detectar falha em qualquer condição, mesmo com usuário ativo.

### shouldMatchWhenUserHasRequiredRoleAndTargetDepartment
- **Cenário:** Política com condições para role e departamento do alvo.
- **Preparação:** Usuário com role "ENFERMEIRO" e departamento UTI; alvo na mesma unidade.
- **Verificações:**
  - Avaliador retorna `true`, comprovando suporte a múltiplas condições simultâneas.

### shouldFailWhenTargetTagsDoNotContainAllRequiredValues
- **Cenário:** Política exige presença de todas as tags `"URGENTE"` e `"CRITICO"`.
- **Preparação:** Alvo possui tags diferentes (`"sensivel"`, `"alto_risco"`).
- **Verificações:**
  - Avaliador retorna `false` por não encontrar todas as tags obrigatórias.

### shouldResolveDynamicTokensForCurrentUserId
- **Cenário:** Condição utiliza token dinâmico `CURRENT_USER_ID` para comparar com o alvo.
- **Preparação:** Alvo expõe método `getOwnerId()` retornando o mesmo ID do usuário autenticado.
- **Verificações:**
  - Avaliador retorna `true`, comprovando a resolução correta de tokens dinâmicos.

### shouldAllowNotInWhenTargetAttributeMissing
- **Cenário:** Operador `NOT_IN` aplicado a atributo inexistente no alvo.
- **Verificações:**
  - Avaliador retorna `true`, tratando ausência do atributo como não pertencente ao conjunto proibido.

### shouldTreatNullRoleConditionAsNoRestrictionForNotIn
- **Cenário:** Condição `NOT_IN` com valor nulo para roles.
- **Verificações:**
  - Avaliador retorna `true`, entendendo que não há restrição quando o valor da condição está vazio.

## Dados de Apoio
- Métodos auxiliares `policyWithConditions` e `condition` facilitam a montagem de políticas e condições reutilizáveis.
- Classe interna `NcTarget` modela um alvo rico com departamento, proprietário, tenant e tags para simular avaliações complexas.
