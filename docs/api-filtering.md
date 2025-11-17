# API filtering cheatsheet

The following endpoints now expose optional query parameters that can be combined with the existing Spring `Pageable` parameters (`page`, `size`, `sort`) to narrow the listing results.

## GET `/api/roles`

| Parameter | Type | Description |
|-----------|------|-------------|
| `name` | `string` | Performs a case-insensitive contains search against the role name. |
| `description` | `string` | Performs a case-insensitive contains search against the role description. |

## GET `/api/permissions`

| Parameter | Type | Description |
|-----------|------|-------------|
| `resource` | `ResourceType` | Restricts the list to a single resource type. |
| `action` | `Action` | Restricts the list to a single action. |
| `feature` | `string` | Case-insensitive contains search applied to the permission feature. |
| `code` | `string` | Case-insensitive contains search applied to the permission code. |

## GET `/api/role-permissions`

| Parameter | Type | Description |
|-----------|------|-------------|
| `roleId` | `long` | Returns only entries that belong to the specified role. |
| `permissionId` | `long` | Returns only entries that point to the specified permission. |

## GET `/api/policies`

| Parameter | Type | Description |
|-----------|------|-------------|
| `resource` | `ResourceType` | Restricts the list to a single resource type. |
| `action` | `Action` | Restricts the list to a single action. |
| `feature` | `string` | Case-insensitive contains search applied to the feature scope. |
| `effect` | `Effect` | Filters policies by their effect (ALLOW/DENY). |
| `enabled` | `boolean` | When informed, returns only enabled/disabled policies. |
| `description` | `string` | Case-insensitive contains search applied to the description. |

## GET `/api/users`

| Parameter | Type | Description |
|-----------|------|-------------|
| `username` | `string` | Case-insensitive contains search against the username. |
| `fullName` | `string` | Case-insensitive contains search against the full name. |
| `status` | `UserStatus` | Filters by the current user status. |
| `origin` | `IdentityOrigin` | Filters by the provisioning origin. |
| `tenantId` | `long` | Allows platform administrators (no tenant bound) to focus on a specific tenant. Tenant-scoped users always receive their own tenant, regardless of the value informed. |

> **Note:** Each endpoint keeps enforcing the tenant scope constraints that already existed. These new parameters only narrow the result set inside the tenant the caller is allowed to see.
