# QualitasCareApiApplicationTests

Documentação do teste de inicialização da aplicação localizado em `com.erp.qualitascareapi.QualitasCareApiApplicationTests`.

## Contexto Geral
- **Tipo de teste:** Integração leve com Spring Boot (`@SpringBootTest`).
- **Perfil utilizado:** `test`.
- **Objetivo:** Validar que o contexto completo da aplicação é carregado sem falhas de configuração.

## Caso de Teste

### contextLoads
- **Cenário:** Inicialização padrão do `ApplicationContext` da aplicação.
- **Verificação:** A execução do teste sem exceções confirma que todas as dependências e beans obrigatórios são registrados corretamente.
