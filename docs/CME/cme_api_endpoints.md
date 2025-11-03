# API do Módulo CME

Este documento descreve os endpoints REST expostos para operar o módulo de Central de Material e Esterilização (CME) do QualitasCare.

## Convenções Gerais

- Todas as rotas estão sob o prefixo `/api/cme`.
- Os endpoints suportam paginação via parâmetros `page`, `size` e `sort` quando retornam `Page<T>`.
- Datas utilizam o formato ISO-8601 (`YYYY-MM-DD`) e carimbos de data/hora seguem `YYYY-MM-DDTHH:MM:SS`.
- Os campos de identificação referenciam registros previamente criados (por exemplo, `tenantId`, `userId`).

## Catálogo de Instrumentais e Kits

### `POST /api/cme/instrumentos`
Cria um instrumento reutilizável.

**Request body**
```json
{
  "tenantId": 1,
  "nome": "Pinça Backhaus",
  "codigoHospitalar": "MAT-001",
  "descricao": "Pinça perfurante"
}
```

**Response** `201 Created`
```json
{
  "id": 10,
  "tenantId": 1,
  "nome": "Pinça Backhaus",
  "codigoHospitalar": "MAT-001",
  "descricao": "Pinça perfurante"
}
```

### `GET /api/cme/instrumentos`
Lista instrumentos cadastrados (paginado).

### `POST /api/cme/kits`
Cadastra um kit de procedimento.

### `GET /api/cme/kits`
Lista kits existentes.

### `POST /api/cme/kits/versoes`
Define uma versão do kit com vigência e validade padrão.

### `GET /api/cme/kits/versoes`
Lista versões registradas.

### `POST /api/cme/kits/itens`
Relaciona instrumentos à versão do kit.

### `GET /api/cme/kits/itens`
Lista itens de kit cadastrados.

## Gestão de Setores, Lotes e Movimentações

### `POST /api/cme/setores`
Registra um setor assistencial para rastreabilidade logística.

### `GET /api/cme/setores`
Lista setores cadastrados.

### `POST /api/cme/lotes`
Cria um lote/etiqueta para rastreio de kits esterilizados.

### `GET /api/cme/lotes`
Lista lotes existentes.

### `POST /api/cme/movimentacoes`
Registra uma movimentação de lote entre setores (entrada contaminada, envio estéril, retorno etc.).

### `GET /api/cme/movimentacoes`
Lista movimentações registradas.

## Autoclaves, Ciclos e Indicadores

### `POST /api/cme/autoclaves`
Cadastra uma autoclave.

### `GET /api/cme/autoclaves`
Lista autoclaves.

### `POST /api/cme/autoclaves/planos`
Define plano preventivo da autoclave (periodicidade, ciclos).

### `GET /api/cme/autoclaves/planos`
Lista planos preventivos.

### `POST /api/cme/autoclaves/manutencoes`
Registra manutenção (preventiva/corretiva/calibração) com evidências.

### `GET /api/cme/autoclaves/manutencoes`
Lista manutenções realizadas.

### `POST /api/cme/autoclaves/higienizacoes-profundas`
Registra higienização profunda mensal da autoclave.

### `GET /api/cme/autoclaves/higienizacoes-profundas`
Lista higienizações profundas.

### `POST /api/cme/higienizacoes-ultrassonica`
Registra checklist diário da lavadora ultrassônica.

### `GET /api/cme/higienizacoes-ultrassonica`
Lista registros de higienização ultrassônica.

### `POST /api/cme/testes/bowie-dick`
Registra teste Bowie-Dick do dia, com resultado e evidências.

### `GET /api/cme/testes/bowie-dick`
Lista testes Bowie-Dick registrados.

### `POST /api/cme/ciclos`
Registra ciclo de esterilização, vinculando autoclave e lote.

### `GET /api/cme/ciclos`
Lista ciclos de esterilização.

### `POST /api/cme/indicadores/quimicos`
Registra indicadores químicos do ciclo.

### `GET /api/cme/indicadores/quimicos`
Lista indicadores químicos registrados.

### `POST /api/cme/indicadores/biologicos`
Registra indicadores biológicos do ciclo.

### `GET /api/cme/indicadores/biologicos`
Lista indicadores biológicos.

## Saneantes

### `POST /api/cme/saneantes`
Registra lote de saneante peracético.

### `GET /api/cme/saneantes`
Lista lotes de saneante.

### `POST /api/cme/saneantes/usos`
Registra uso/diluição de saneante em uma etapa do processo.

### `GET /api/cme/saneantes/usos`
Lista usos registrados.

## Qualidade, Exames e Resíduos

### `POST /api/cme/exames-cultura`
Registra exame de cultura com evidências anexas.

### `GET /api/cme/exames-cultura`
Lista exames cadastrados.

### `POST /api/cme/nao-conformidades`
Abre uma não conformidade específica da CME.

### `GET /api/cme/nao-conformidades`
Lista não conformidades.

### `POST /api/cme/geracoes-residuo`
Registra geração de resíduo associada a lote ou saneante.

### `GET /api/cme/geracoes-residuo`
Lista registros de geração de resíduos.

## Campos de Identificação

- `tenantId` e `tenant_id`: referência ao hospital/cliente.
- `responsavelId`, `montadoPorId`, `usadoPorId`, `executadoPorId`, `liberadoPorId`: referência a `users.id`.
- `evidenciasIds`: lista de IDs de `evidencias_arquivo` previamente carregados.
- `kitVersaoId`, `loteId`, `autoclaveId`, `cicloId`: referência às entidades do domínio CME.

## Considerações

- O fluxo de liberação de ciclos deve registrar testes e indicadores com `resultado` igual a `CONFORME` para que o lote seja liberado.
- Os endpoints aceitam anexar evidências digitais via IDs do repositório central `evidencias_arquivo`.
- Para rastreabilidade completa, mantenha a sequência: higienização ➜ testes ➜ ciclo ➜ movimentação ➜ qualidade.
