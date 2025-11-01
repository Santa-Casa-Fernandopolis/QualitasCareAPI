# AllTestsSuite

Documentação da suíte de testes `com.erp.qualitascareapi.AllTestsSuite`.

## Contexto Geral
- **Anotação:** `@Suite` com `@SelectPackages("com.erp.qualitascareapi")`.
- **Objetivo:** Fornecer ponto único para executar todos os testes JUnit dentro do pacote raiz da aplicação.

## Comportamento
- Ao ser executada, a suíte descobre automaticamente todas as classes de teste dentro do pacote `com.erp.qualitascareapi` e subpacotes.
- Não define casos de teste próprios; funciona como agregador para facilitar execuções completas (por exemplo, em IDEs ou pipelines que não detectam automaticamente as classes individuais).
