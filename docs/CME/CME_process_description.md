# 🏥 Fluxo Operacional da Central de Material e Esterilização (CME)

> Documento de referência técnica do sistema **QualitasCare – Módulo CME**  
> Baseado no diagrama de processos e nas normas **RDC 15/2012**, **RDC 222/2018**, **ISO 11140-1**, **ISO 11138-1**, e nos critérios da **certificação ONA**.

---

## 🎯 **Objetivo do Fluxo**
Garantir o **reprocessamento seguro e rastreável** de todos os artigos médico-hospitalares, assegurando a **eficácia da esterilização**, a **integridade dos materiais**, e o **cumprimento das boas práticas da qualidade**.

O processo é totalmente integrado ao sistema **QualitasCare**, que registra, rastreia e audita cada etapa com base nas entidades modeladas no domínio **CME**.

---

## 🔁 **Visão Geral do Processo**
O fluxo da CME é composto por **sete macroetapas principais**, que cobrem o ciclo completo do material:  
**Recebimento → Limpeza → Montagem → Esterilização → Armazenamento → Distribuição → Retorno Contaminado.**

Cada etapa é registrada digitalmente, com rastreabilidade por **etiqueta e QR Code** (`LoteEtiqueta`) e controle de responsabilidade por **usuário autenticado** (`User`).

---

## 🧩 **Etapas do Fluxo**

### 1️⃣ Recebimento de Material Contaminado

**Objetivo:** registrar a entrada de artigos contaminados oriundos de diferentes setores.

- **Responsável:** equipe da CME – área suja/expurgo.
- **Registros no sistema:**
    - Origem (`SetorOrigem`);
    - Data/hora da entrada;
    - Responsável pelo recebimento (`User`);
    - Condição do material.
- **Entidades envolvidas:** `MovimentacaoKit`, `Setor`.

**Validação:**  
→ Caso o material apresente irregularidades (ex.: ausência de identificação ou danos), é aberto um registro de **não conformidade** (`NaoConformidadeCME`).

---

### 2️⃣ Pré-limpeza, Limpeza e Descontaminação

**Objetivo:** eliminar matéria orgânica e reduzir a carga microbiana antes da montagem.

- **Etapas controladas:**
    - Lavagem manual ou mecânica;
    - Lavagem ultrassônica;
    - Enxágue e secagem.
- **Checklist obrigatório:**
    - Registro diário da **Higienização da Lavadora Ultrassônica** (`HigienizacaoUltrassonica`).

**Regras de sistema:**
- A liberação de ciclos de esterilização é **bloqueada** caso o checklist do dia **não esteja registrado**.

---

### 3️⃣ Montagem e Empacotamento dos Kits

**Objetivo:** reorganizar os instrumentais conforme os kits cirúrgicos padronizados.

- **Referência:** `KitCirurgico` (lista de peças e quantidades esperadas).
- **Campos obrigatórios na montagem (`LoteEtiqueta`):**
    - Data e hora do empacotamento;
    - Nome e COREN do colaborador;
    - Conferência de pérfuros e integridade das peças;
    - Geração automática do **QR Code de rastreabilidade**.
- **Controle de validade:**
    - Cada lote recebe uma **data de validade** (conforme tipo de embalagem e POP vigente).

---

### 4️⃣ Esterilização

**Objetivo:** garantir destruição total de micro-organismos em condições validadas.

#### a) **Preparação da Autoclave**
- Checagem do equipamento (`Autoclave`);
- Registro de **Higienização Profunda Mensal** (`HigienizacaoAutoclaveProfunda`).

#### b) **Teste Bowie-Dick (BD)**
- Verifica a **remoção do ar** e a **penetração de vapor saturado**.
- Executado **no primeiro ciclo do dia**.
- Resultado registrado em `TesteBowieDick`.
- Se **falhar**, bloqueia a execução de novos ciclos.

#### c) **Ciclo de Esterilização**
- Cada carga é registrada em `CicloEsterilizacao`:
    - Código da autoclave;
    - Data/hora de início e término;
    - Parâmetros físicos (tempo, temperatura, pressão);
    - Operador responsável (`User`).

#### d) **Indicadores Químicos (CI)**
- Registrados em `IndicadorQuimico`.
- Verificam exposição às condições adequadas do ciclo (mudança de cor).

#### e) **Indicadores Biológicos (BI)**
- Registrados em `IndicadorBiologico`.
- Confirmam a **eficácia do processo** (ausência de crescimento microbiano).

#### f) **Critério de Liberação**
- O lote (`LoteEtiqueta`) só é liberado (`status = LIBERADO`) se:
    - BD = OK
    - CI = OK
    - BI = OK (quando aplicável)
    - Higienização da autoclave vigente
    - Checklist ultrassônico do dia concluído.

---

### 5️⃣ Armazenamento e Distribuição

**Objetivo:** garantir conservação e rastreabilidade dos materiais até o uso.

- **Armazenamento:** área limpa, identificada e controlada.
- **Distribuição:** feita mediante **movimentação registrada** (`MovimentacaoKit`):
    - Setor de destino;
    - Data e hora;
    - Responsável pela entrega e recebimento.

- **Validade monitorada:** o sistema emite alertas automáticos para lotes próximos do vencimento.

---

### 6️⃣ Uso e Retorno de Material Contaminado

**Objetivo:** fechar o ciclo do material.

- Após o uso, o material retorna à CME como **“contaminado”**.
- Evento registrado como nova `MovimentacaoKit` com tipo `RETORNO_CONTAMINADO`.
- Esse registro vincula o mesmo **QR Code** do lote original, garantindo **rastreabilidade reversa** (CME → Setor → CME).

---

### 7️⃣ Controle de Qualidade e Auditoria

**Objetivo:** monitorar desempenho, segurança e conformidade.

- **Inspeções diárias:** `InspecaoPecas` (nº de caixas, peças e avulsos conferidos).
- **Culturas:** `ExameCultura` — coleta e resultado de amostras de kits, autoclaves ou superfícies.
- **Não Conformidades:** `NaoConformidadeCME` — abertura automática quando falhas são registradas.
- **Indicadores de Desempenho:**
    - % de ciclos com BD/BI/CI conformes
    - % de kits rastreados completos
    - Taxa de reprocesso
    - Cumprimento de higienização (autoclave e ultrassônica)
    - Culturas positivas por 1.000 kits processados

---

## 🧠 **Regras de Negócio do Sistema**

1. **Bloqueio de ciclo** se BD ou BI falhar.
2. **Bloqueio de liberação de lote** se qualquer teste estiver “não conforme”.
3. **Obrigatoriedade de checklist diário** (lavadora ultrassônica).
4. **Controle automático de validade dos kits**.
5. **Auditoria automática** de todas as ações (usuário, data, IP).
6. **Alertas automáticos** para:
    - Autoclaves sem limpeza profunda mensal;
    - Saneantes com prazo de validade vencendo;
    - Kits prestes a vencer;
    - Falhas de indicadores.

---

## 📊 **Integração com a Gestão da Qualidade e ONA**

| Requisito ONA | Como o sistema atende |
|----------------|-----------------------|
| **Rastreabilidade total** | QR Code + logs de ciclo + operador + lote |
| **Segurança do paciente** | Bloqueio de liberação em caso de falhas |
| **Indicadores de processo** | BI, CI, BD, TAT e taxa de reprocesso |
| **Gestão ambiental (PGRSS)** | Controle de saneantes e resíduos |
| **Auditoria e melhoria contínua** | Registro digital, não conformidades e histórico de revisões |

---

## 🧩 **Entidades-Chave (resumo)**

| Entidade | Função principal |
|-----------|-----------------|
| `Autoclave` | Equipamento de esterilização. |
| `CicloEsterilizacao` | Registro técnico do processo de esterilização. |
| `TesteBowieDick`, `IndicadorBiologico`, `IndicadorQuimico` | Evidências de eficácia. |
| `LoteEtiqueta` | Identificador único de cada kit, com QR Code e validade. |
| `KitCirurgico`, `Instrumento`, `KitItem` | Estrutura e composição dos materiais. |
| `HigienizacaoUltrassonica`, `HigienizacaoAutoclaveProfunda` | Controle de limpeza de equipamentos. |
| `SaneantePeraceticoLote`, `UsoSaneante` | Controle químico e diluição. |
| `MovimentacaoKit`, `Setor` | Rastreabilidade logística. |
| `ExameCultura`, `NaoConformidadeCME` | Controle de qualidade e segurança. |

---

## ✅ **Conclusão**

O fluxo da CME modelado no sistema **QualitasCare** proporciona:
- **Rastreabilidade ponta a ponta**;
- **Conformidade com a legislação sanitária**;
- **Medição contínua de desempenho e qualidade**;
- **Suporte completo à certificação ONA (níveis 1 a 3)**.

Cada etapa é **mensurável, auditável e correlacionada com o usuário responsável**, permitindo não apenas controle operacional, mas também **gestão estratégica da qualidade hospitalar**.
