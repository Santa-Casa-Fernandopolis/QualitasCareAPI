# Tratamento de Exceções

O serviço expõe respostas de erro consistentes por meio do `GlobalExceptionHandler`. Esta seção descreve o formato e os cenários mais comuns.

## Estrutura padrão de erro
Todos os erros retornam o objeto `ApiError`:
```json
{
  "timestamp": "2024-01-31T12:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "code": "request.invalid",
  "message": "Descrição humana do problema",
  "path": "/api/...",
  "details": { "campo": "mensagem" }
}
```
- `timestamp`: instante em que o erro foi gerado.
- `status` / `error`: código HTTP e descrição padrão.
- `code`: identificador lógico reutilizável.
- `message`: mensagem direcionada ao consumidor.
- `path`: URI original da requisição.
- `details`: mapa opcional com contexto adicional. Quando ausente, é um objeto vazio.

## Exceções de domínio (`ApplicationException`)
Exceções que estendem `ApplicationException` definem o `HttpStatus`, código lógico e detalhes extras. Exemplos concretos:
- `BadRequestException`: falhas de validação de regras de negócio (`400`).
- `ResourceNotFoundException`: recurso inexistente (`404`).
Essas exceções são convertidas diretamente em `ApiError` preservando mensagem e metadados.

## Validações Bean Validation
- **`MethodArgumentNotValidException`**: ocorre quando `@Valid` falha em corpos de requisição. Os campos com erro aparecem em `details` (`{ "campo": "mensagem" }`). Status `400`.
- **`ConstraintViolationException`**: ativa para validações em parâmetros. `details` inclui o caminho da propriedade e mensagem. Status `400`.
- **`MethodArgumentTypeMismatchException`**: tipos incompatíveis em parâmetros de rota/consulta. A resposta inclui `parameter`, `value` e `requiredType` em `details`.

## Violações de integridade
`DataIntegrityViolationException` resulta em status `409 Conflict`, código `data.integrity` e mensagem genérica. A exceção original é registrada em log para diagnóstico.

## Erros não tratados
Qualquer outra exceção gera status `500`, código `internal.error` e mensagem "Unexpected error occurred". Os detalhes são vazios e o erro é registrado no log.

## Boas práticas para clientes
1. Verifique sempre o campo `code` para lógica de tratamento automatizada.
2. Utilize `details` para exibir mensagens específicas por campo ao usuário final.
3. Para chamadas paginadas, valide parâmetros antes de enviá-los e trate `400` em caso de inconsistência.
