# Domain Class Diagrams

The `domain_class_diagrams.puml` file groups the main aggregates by business domain so that cross-module reuse is explicit.

## Domains

- **core** — Estruturas compartilhadas entre módulos (setores, instrumentais e kits) e o modelo de exames de cultura.
- **quality** — Contratos corporativos de não conformidades implementados pelos módulos especializados.
- **environmental** — Entidades responsáveis por rastrear geração de resíduos e suas classes regulamentares.
- **cme** — Objetos específicos da Central de Materiais (lotes, movimentações, controles de qualidade), conectados aos domínios core/quality/environmental.

Render the diagram with:

```bash
plantuml docs/domain_class_diagrams.puml
```
