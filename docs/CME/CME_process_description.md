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
- **Entidades envolvidas:** `Movimentacao`, `Setor`, `GeracaoResiduo` (quando há descarte imediato).

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
    - Upload do checklist assinado/fotografado como **evidência digital** (`EvidenciaArquivo`).

**Regras de sistema:**
- A liberação de ciclos de esterilização é **bloqueada** caso o checklist do dia **não esteja registrado**.

---

### 3️⃣ Montagem e Empacotamento dos Kits

**Objetivo:** reorganizar os instrumentais conforme os kits cirúrgicos padronizados.

- **Referência:** `KitProcedimento` e suas versões vigentes (`KitVersion`) – permitem rastrear alterações na composição.
- **Campos obrigatórios na montagem (`LoteEtiqueta`):**
    - Data e hora do empacotamento;
    - Nome e COREN do colaborador;
    - Conferência de pérfuros e integridade das peças;
    - Geração automática do **QR Code de rastreabilidade**.
- **Controle de validade:**
    - Cada lote recebe uma **data de validade** (conforme tipo de embalagem e POP vigente).
- **Evidências anexas:**
    - Fotos do kit montado, checklist de conferência e registros de conferência anexados em `EvidenciaArquivo`.

---

### 4️⃣ Esterilização

**Objetivo:** garantir destruição total de micro-organismos em condições validadas.

#### a) **Preparação da Autoclave**
- Checagem do equipamento (`Autoclave`);
- Registro de **Higienização Profunda Mensal** (`HigienizacaoAutoclaveProfunda`).

#### b) **Teste Bowie-Dick (BD)**
- Verifica a **remoção do ar** e a **penetração de vapor saturado**.
- Executado **no primeiro ciclo do dia**.
- Resultado registrado em `TesteBowieDick`, com anexos digitais (foto do indicador, relatório do equipamento) via `EvidenciaArquivo`.
- Se **falhar**, bloqueia a execução de novos ciclos.

#### c) **Ciclo de Esterilização**
- Cada carga é registrada em `CicloEsterilizacao`:
    - Código da autoclave;
    - Data/hora de início e término;
    - Parâmetros físicos (tempo, temperatura, pressão);
    - Operador responsável (`User`).

#### d) **Indicadores Químicos (CI)**
- Registrados em `IndicadorQuimico`, com anexação das tiras digitalizadas (`EvidenciaArquivo`).
- Verificam exposição às condições adequadas do ciclo (mudança de cor).

#### e) **Indicadores Biológicos (BI)**
- Registrados em `IndicadorBiologico`, incluindo leitura fotográfica e relatório do incubador anexados (`EvidenciaArquivo`).
- Confirmam a **eficácia do processo** (ausência de crescimento microbiano).

#### f) **Critério de Liberação**
- O lote (`LoteEtiqueta`) só é liberado (`status = LIBERADO`) se:
    - BD = OK
    - CI = OK
    - BI = OK (quando aplicável)
    - Higienização da autoclave vigente
    - Checklist ultrassônico do dia concluído.

#### g) **Controle de Manutenção das Autoclaves**
- **Registro centralizado:** todas as ordens de serviço (preventivas, corretivas, calibrações e verificações metrológicas) ficam em `ManutencaoAutoclave`, com vínculo obrigatório à autoclave, tipo, status e responsável técnico.
- **Planos preventivos parametrizáveis:** `PlanoPreventivoAutoclave` define periodicidades por tempo (dias) e contagem de ciclos (`limiteCiclos`), disparando alertas conforme consumo (`contadorCiclosAtual`) calculado automaticamente a partir de `CicloEsterilizacao`.
- **Workflow digital:** cada manutenção registra abertura, execução e encerramento, exigindo anexos técnicos (`EvidenciaArquivo`) e, quando aplicável, certificados de calibração e laudos de segurança elétrica.
- **Bloqueio inteligente:** quando um plano preventivo expira ou o contador excede o limite, a autoclave recebe status `BLOQUEADA_MANUTENCAO`; novos ciclos só são autorizados após manutenção concluída ou liberação justificável do engenheiro clínico, com rastreabilidade do responsável.
- **Integrações:** manutenções críticas geram `NaoConformidadeCME` automaticamente, permitindo planos de ação corporativos; paradas prolongadas também atualizam a fila de redistribuição de cargas e notificações para equipes assistenciais.
- **Indicadores:** dashboards mostram disponibilidade por autoclave, tempo médio para conclusão de manutenção e aderência aos planos preventivos.

---

### 5️⃣ Armazenamento e Distribuição

**Objetivo:** garantir conservação e rastreabilidade dos materiais até o uso.

- **Armazenamento:** área limpa, identificada e controlada.
- **Distribuição:** feita mediante **movimentação registrada** (`Movimentacao`):
    - Setor de destino;
    - Data e hora;
    - Responsável pela entrega e recebimento.

- **Validade monitorada:** o sistema emite alertas automáticos para lotes próximos do vencimento.

---

### 6️⃣ Uso e Retorno de Material Contaminado

**Objetivo:** fechar o ciclo do material.

- Após o uso, o material retorna à CME como **“contaminado”**.
- Evento registrado como nova `Movimentacao` com tipo `RETORNO_CONTAMINADO`.
- Esse registro vincula o mesmo **QR Code** do lote original, garantindo **rastreabilidade reversa** (CME → Setor → CME).

---

### 7️⃣ Controle de Qualidade e Auditoria

**Objetivo:** monitorar desempenho, segurança e conformidade.

- **Inspeções diárias:** `InspecaoMaterial` (nº de caixas, peças e avulsos conferidos).
- **Culturas:** `ExameCultura` — coleta e resultado de amostras de kits, autoclaves ou superfícies.
- **Laudos laboratoriais:** anexados ao `ExameCultura` por meio de `EvidenciaArquivo` (PDF, imagens, documentos estruturados).
- **Não Conformidades:** `NaoConformidadeCME` — abertura automática quando falhas são registradas.
- **Planos de ação corporativos:** a `NaoConformidadeCME` herda `NaoConformidadeBase` (Qualidade) e gera/atualiza `PlanoAcaoQualidade`, com anexação de relatórios de investigação, atas e evidências de conclusão.
- **Gestão ambiental:** falhas com descarte de materiais ou saneantes geram vínculos com `GeracaoResiduo` para rastrear manifestos e destino final.
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
    - Autoclaves com manutenção preventiva atrasada;
    - Saneantes com prazo de validade vencendo;
    - Kits prestes a vencer;
    - Falhas de indicadores.
7. **Acionamento automático de planos de ação** (Qualidade) sempre que a severidade da não conformidade exigir investigação formal.
8. **Registro centralizado de evidências** (imagens, PDFs, laudos) obrigatório para liberações críticas e auditorias.

---

## 📁 **Repositório central de evidências**

O módulo CME utiliza a entidade `EvidenciaArquivo` (pacote core) para consolidar **imagens, PDFs e documentos laboratoriais**. Os anexos ficam relacionados a testes (BD/CI/BI), checklists, higienizações, não conformidades, planos de ação e exames de cultura. Cada evidência guarda tipo, autor, hash e URI do arquivo, garantindo autenticidade e rastreabilidade para auditorias internas, ONA e vigilância sanitária.

---

## 🌱 **Integração com PGRSS e Qualidade Corporativa**

- **GeracaoResiduo (ambiental):** vincula descartes oriundos de usos de saneantes, perdas de material ou reprovações de kits, permitindo acompanhar peso estimado, classe de resíduo e manifesto de coleta.
- **NaoConformidadeBase / PlanoAcaoQualidade:** padronizam investigação, tratativas, responsáveis e prazos, com indicadores corporativos compartilhados com outros módulos (Farmácia, CC, Hotelaria).
- **Evidências compartilhadas:** relatórios ambientais, comprovantes de destinação, atas de reunião e fotos de correções ficam anexadas ao mesmo repositório digital.

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
| `TesteBowieDick`, `IndicadorBiologico`, `IndicadorQuimico` | Evidências de eficácia com anexos digitais. |
| `LoteEtiqueta` | Identificador único de cada kit, com QR Code e validade. |
| `KitProcedimento`, `KitVersion`, `KitItem`, `Instrumento` | Estrutura modular e versionada dos materiais. |
| `HigienizacaoUltrassonica`, `HigienizacaoAutoclaveProfunda` | Controle de limpeza de equipamentos com checklist digital. |
| `ManutencaoAutoclave`, `PlanoPreventivoAutoclave` | Gestão de ordens de serviço, planos preventivos e calibrações. |
| `SaneantePeraceticoLote`, `UsoSaneante`, `GeracaoResiduo` | Controle químico, diluição e descarte ambiental. |
| `Movimentacao`, `Setor` | Rastreabilidade logística. |
| `ExameCultura`, `EvidenciaArquivo` | Gestão de laudos laboratoriais e anexos. |
| `NaoConformidadeCME`, `NaoConformidadeBase`, `PlanoAcaoQualidade` | Gestão corporativa de não conformidades e planos de ação. |

---

## ✅ **Conclusão**

O fluxo da CME modelado no sistema **QualitasCare** proporciona:
- **Rastreabilidade ponta a ponta**;
- **Conformidade com a legislação sanitária**;
- **Medição contínua de desempenho e qualidade**;
- **Suporte completo à certificação ONA (níveis 1 a 3)**.

Cada etapa é **mensurável, auditável e correlacionada com o usuário responsável**, permitindo não apenas controle operacional, mas também **gestão estratégica da qualidade hospitalar**.
