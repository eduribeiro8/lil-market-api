---
name: lilmarket-endpoint-scaffold
description: >-
  Use this skill when creating or modifying REST endpoints, CRUD modules, DTOs,
  MapStruct mappers, domain exceptions, or RBAC permissions in LilMarket API.
  Ensures mandatory registration in SecurityConfig, GlobalExceptionHandler,
  messages.properties, and OpenAPI annotations.
---

# LilMarket Endpoint & Module Scaffold

Follow this vertical checklist whenever adding or modifying a resource or endpoint so no cross-cutting registration is missed.

## Step-by-Step Workflow

### 1. DTOs & Validation Messages
- Define request/response records in `src/main/java/com/eduribeiro8/LilMarket/dto/` (`*RequestDTO`, `*ResponseDTO`).
- Annotate both record and fields with `@Schema` in **Portuguese** (`description`, `example`, `requiredMode`).
- When adding localized Bean Validation messages (`message = "{domain.field.rule}"`), add the corresponding key to `src/main/resources/messages.properties` (configured via `ValidationConfig`).

### 2. MapStruct Mapper
- Define or update the interface in `src/main/java/com/eduribeiro8/LilMarket/mapper/` with `@Mapper(componentModel = "spring")`.
- Ignore audit/server-managed fields (`createdAt`, `updatedAt`, `id`) on `toEntity` and `updateEntityFromDTO` (`@MappingTarget`).
- Resolve foreign-key entities (`ProductCategory`, `Supplier`, etc.) in the `*ServiceImpl` layer rather than doing repository lookups inside MapStruct.

### 3. Custom Exceptions & GlobalExceptionHandler (Trap Alert)
- Create domain exceptions in `src/main/java/com/eduribeiro8/LilMarket/rest/exception/` extending `RuntimeException` or `BusinessException`.
- **Mandatory registration:** `GlobalExceptionHandler.java` explicitly enumerates exception classes inside `@ExceptionHandler({...})` arrays:
  - `404 Not Found`: add new `*NotFoundException.class` to `handleNotFound`.
  - `409 Conflict`: add new `Duplicate*Exception.class` to `handleDuplicate`.
  - `422 Unprocessable Entity`: extend `BusinessException` (handled by `handleBusinessException`) or add a dedicated handler returning `ErrorResponse`.
- Omitting a new exception from `GlobalExceptionHandler` causes uncaught exceptions to fall through as `500 Internal Server Error`.

### 4. Controller & OpenAPI Contract
- Place the controller in `src/main/java/com/eduribeiro8/LilMarket/rest/`.
- Note that `application.properties` sets `server.servlet.context-path=/api`: **do not** prefix `@RequestMapping` or `@GetMapping` paths with `/api` (e.g., use `"/supplier"`, which exposes `/api/supplier`).
- Include on the controller/endpoints:
  - `@Tag(name = "...", description = "...")` (Portuguese description)
  - `@SecurityRequirement(name = "bearerAuth")` (omit only for public auth endpoints)
  - `@Operation(summary = "...", description = "...")` in Portuguese
  - `@ApiStandardErrors` (provides standard `401`, `403`, `500` responses)
  - Specific `@ApiResponses` (`200`/`201`/`204`, `400`, `404`, `409`, `422`) referencing `ErrorResponse.class`
  - `@Valid @RequestBody` on all write payloads

### 5. SecurityConfig RBAC Registration (Trap Alert)
- Inspect `src/main/java/com/eduribeiro8/LilMarket/security/SecurityConfig.java`.
- Routes are matched **explicitly per `HttpMethod`** (`GET`, `POST`, `PUT`, `DELETE`), and any unmatched path falls through to `.anyRequest().hasRole("ADMIN")`.
- Whenever adding a new root path (e.g., `/promotion/**`) or an operation restricted to `MANAGER`/`ADMIN` (like `/customer/*/deposit` or `/report/*`), place the specific `requestMatchers(...)` **before** the generic role blocks and catch-all rule.
  - Note: matchers in `SecurityConfig` are relative to the servlet context-path (do **not** include `/api`).

## Validation Steps

1. Run `./mvnw compile` to trigger Lombok + `lombok-mapstruct-binding` + MapStruct processor.
2. Inspect generated mapper classes under `target/generated-sources/annotations/com/eduribeiro8/LilMarket/mapper/` to confirm no target fields were accidentally left unmapped.
3. Run relevant unit tests via `./mvnw test -Dtest=*ServiceImplTest`.
