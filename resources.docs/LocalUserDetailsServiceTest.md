# LocalUserDetailsServiceTest

Documentação dos cenários de teste implementados em `com.erp.qualitascareapi.iam.application.LocalUserDetailsServiceTest`.

## Contexto Geral
- **Classe sob teste:** `LocalUserDetailsService`
- **Dependências simuladas:** `UserRepository`
- **Objetivo:** Garantir a resolução correta do usuário autenticado considerando variações de entrada do nome de usuário e tratamento de exceções.

## Casos de Teste

### loadUserByUsername_resolvesTenantSuffixWithAtSymbol
- **Cenário:** Entrada contendo sufixo de tenant separado por `@` e espaços extras.
- **Preparação:** O repositório retorna um usuário válido quando consultado com `findByUsernameIgnoreCaseAndTenant_CodeIgnoreCase("enf.scf", "scf")`.
- **Verificações:**
  - O método retorna uma instância de `AuthenticatedUserDetails` com username `"enf.scf"`.
  - Garante que apenas a consulta específica por tenant foi realizada, sem fallback para busca genérica.

### loadUserByUsername_resolvesTenantPrefixSeparatedByPipe
- **Cenário:** Entrada contendo prefixo de tenant separado por `|`.
- **Preparação:** Repositório configurado para responder à busca por username e tenant com `"SCF"`.
- **Verificações:**
  - Username retornado é `"admin.scf"`.
  - Captura do parâmetro de tenant confirma resolução para `"SCF"`.
  - Nenhuma chamada para `findByUsernameIgnoreCase` (busca genérica).

### loadUserByUsername_withoutTenantFallsBackToGenericLookup
- **Cenário:** Username informado sem tenant explícito.
- **Preparação:** Repositório retorna usuário na busca genérica `findByUsernameIgnoreCase("enf.scf")`.
- **Verificações:**
  - Username retornado é `"enf.scf"`.
  - A interação com o repositório ocorre apenas via busca genérica.

### loadUserByUsername_rejectsBlankInput
- **Cenário:** Entrada vazia composta apenas por espaços.
- **Verificações:**
  - Lança `UsernameNotFoundException`.
  - Nenhuma interação com o repositório.

### loadUserByUsername_throwsWhenRepositoryReturnsEmpty
- **Cenário:** Username inexistente.
- **Preparação:** Repositório retorna `Optional.empty()` para `"ghost"`.
- **Verificações:**
  - Lança `UsernameNotFoundException` indicando que o usuário não foi encontrado.

## Dados de Apoio
- O método auxiliar `buildUser` monta um usuário ativo local com role "ENFERMEIRO" para reutilização nos cenários válidos.
