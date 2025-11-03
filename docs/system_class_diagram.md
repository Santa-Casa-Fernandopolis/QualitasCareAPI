# System Class Diagram

The `system_class_diagram.puml` file describes the main building blocks of the QualitasCare API. It organizes the system into configuration, IAM, security, core, quality, environmental, CME, and observability packages, and highlights the controllers, services, repositories, and domain entities that collaborate in each area. Domain classes list their core attributes to make the persistence model easier to understand, and REST controllers enumerate the HTTP endpoints they expose.

## Rendering the diagram

Use PlantUML to render the diagram into an image:

```bash
plantuml docs/system_class_diagram.puml
```

## Module overview

- **Configuration**: Bootstraps Spring Security, the authorization server, and supporting beans such as the initial data loader.
- **IAM**: Manages tenants and users through REST controllers backed by services and repositories that persist `Tenant` and `User` entities.
- **Security**: Provides role, permission, policy, and override management along with the access-decision services that consume those models at runtime.
- **Core**: Centraliza entidades transversais (`Setor`, `Instrumento`, kits e exames de cultura) e seus repositórios reutilizados por múltiplos módulos.
- **Quality**: Define o contrato corporativo de não conformidades (`NaoConformidadeBase`, `TipoNaoConformidade`, `NaoConformidadeStatus`).
- **Environmental**: Agrupa o modelo de geração de resíduos hospitalares (`GeracaoResiduo`, `ClasseResiduo`).
- **CME**: Expõe as APIs e serviços específicos da central de materiais, consumindo os domínios Core/Quality/Environmental.
- **Observability**: Groups the security, data, and operational logging components used to audit and trace activity across the platform.
- **REST endpoints**: Cada entrada de controller detalha os verbos e caminhos HTTP servidos pela API, fornecendo uma visão rápida da superfície disponível sem precisar inspecionar o código Java.

Refer to the PlantUML source for the detailed relationships between classes.
