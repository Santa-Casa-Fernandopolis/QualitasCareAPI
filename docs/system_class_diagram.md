# System Class Diagram

The `system_class_diagram.puml` file describes the main building blocks of the QualitasCare API. It organizes the system into configuration, IAM, security, and observability packages, and highlights the controllers, services, repositories, and domain entities that collaborate in each area.

## Rendering the diagram

Use PlantUML to render the diagram into an image:

```bash
plantuml docs/diagrams/system_class_diagram.puml
```

## Module overview

- **Configuration**: Bootstraps Spring Security, the authorization server, and supporting beans such as the initial data loader.
- **IAM**: Manages tenants and users through REST controllers backed by services and repositories that persist `Tenant` and `User` entities.
- **Security**: Provides role, permission, policy, and override management along with the access-decision services that consume those models at runtime.
- **Observability**: Groups the security, data, and operational logging components used to audit and trace activity across the platform.

Refer to the PlantUML source for the detailed relationships between classes.
