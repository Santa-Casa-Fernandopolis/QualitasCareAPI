# Modulo SAME

## Objetivo

O modulo SAME centraliza pacientes, identificadores de prontuario e documentos clinicos digitalizados para consulta assistencial e administrativa.

Os PDFs digitalizados nascem sem valor legal definido. O valor padrao usado pelo backend e:

`Copia digitalizada para consulta administrativa e assistencial, sem valor legal definido.`

## Principais APIs

- `POST /api/same/patients`
- `GET /api/same/patients/{id}`
- `GET /api/same/patients/search`
- `GET /api/same/patients/match-suggestions`
- `POST /api/same/patients/{patientId}/identifiers`
- `GET /api/same/patients/{patientId}/identifiers`
- `DELETE /api/same/patients/{patientId}/identifiers/{identifierId}`
- `POST /api/same/documents/upload`
- `GET /api/same/documents/{id}`
- `GET /api/same/documents/{id}/download`
- `GET /api/same/documents/search`
- `PUT /api/same/documents/{id}/metadata`
- `PATCH /api/same/documents/{id}/archive`
- `PATCH /api/same/documents/{id}/block`
- `GET /api/same/audit/documents/{documentId}`
- `GET /api/same/audit/patients/{patientId}`

## Integracoes Legadas

Wireline e Save sao configurados pelo proprio modulo SAME:

- `POST /api/same/legacy-sources`
- `GET /api/same/legacy-sources`
- `GET /api/same/legacy-sources/{id}`
- `PUT /api/same/legacy-sources/{id}`
- `PATCH /api/same/legacy-sources/{id}/status`
- `POST /api/same/legacy-sources/{id}/test-connection`

O Soul MV nao deve ser cadastrado como fonte legada SAME. Ele usa a configuracao existente do modulo MV:

- `MV_DB_URL`
- `MV_DB_USERNAME`
- `MV_DB_PASSWORD`

## Consultas E Snapshots Legados

As consultas abaixo escolhem automaticamente o conector correto para `SOUL_MV`, `WIRELINE` ou `SAVE` e persistem um snapshot da resposta:

- `GET /api/same/legacy-patients/search/by-cpf`
- `GET /api/same/legacy-patients/search/by-record`
- `GET /api/same/legacy-patients/search/by-name-birth-date`
- `GET /api/same/legacy-patients/search/by-external-id`
- `GET /api/same/legacy-patients/snapshots`

## SQL Configuravel

Cada conector JDBC pode receber consultas customizadas via `application.properties`.

Padrao das chaves:

- `same.legacy.wireline.query-by-cpf`
- `same.legacy.wireline.query-by-medical-record-code`
- `same.legacy.wireline.query-by-name-birth-date`
- `same.legacy.wireline.query-by-external-id`
- `same.legacy.save.query-by-cpf`
- `same.legacy.save.query-by-medical-record-code`
- `same.legacy.save.query-by-name-birth-date`
- `same.legacy.save.query-by-external-id`
- `same.legacy.soul-mv.query-by-cpf`
- `same.legacy.soul-mv.query-by-medical-record-code`
- `same.legacy.soul-mv.query-by-name-birth-date`
- `same.legacy.soul-mv.query-by-external-id`

As consultas devem retornar, quando possivel, os aliases:

- `external_patient_id`
- `medical_record_code`
- `full_name`
- `mother_name`
- `birth_date`
- `cpf`
- `cns`
- `sex`
- `raw_payload_json`

## Armazenamento

Configuracoes principais:

- `same.storage.path=/data/ged-same/documents`
- `same.storage.max-file-size-bytes=52428800`

O backend grava o arquivo com nome tecnico seguro e registra hash SHA-256.

## Perfis Funcionais

O bootstrap de dados de desenvolvimento cria os perfis:

- `SAME_MANAGER`
- `SAME_OPERATOR`
- `CLINICAL_VIEWER`
- `AUDITOR`

Eles sao compostos por permissoes atomicas do modulo, como `SAME_PATIENT`, `SAME_DOCUMENT`, `SAME_AUDIT` e `SAME_LEGACY_SOURCE`.
