---
name: api-docs-contract-sync
description: >-
  Use this skill when documenting endpoints with SpringDoc OpenAPI (Swagger) or
  when auditing and updating src/docs/api-summary.md for front-end consumers in
  LilMarket API. Ensures paths, JWT auth, RBAC roles, DTO payloads, and error
  contracts match the real Java implementation.
---

# OpenAPI & Front-End Contract Synchronization

Keep both in-code Swagger annotations and `src/docs/api-summary.md` aligned with the live codebase.

## Step-by-Step Workflow

### 1. Extract the Live API Surface
Run the inventory helper script from the repository root:
- [list-endpoints.sh](./scripts/list-endpoints.sh)

```bash
bash .agents/skills/api-docs-contract-sync/scripts/list-endpoints.sh
```

This outputs all controller mappings, `SecurityConfig` authorization rules, and `GlobalExceptionHandler` HTTP status mappings.

### 2. Verify Critical Contract Details
When writing or updating documentation in `src/docs/api-summary.md` or controller annotations, check these exact project rules:

1. **Base Context Path:**
   - `src/main/resources/application.properties` sets `server.servlet.context-path=/api`.
   - External clients call `/api/<controller-path>` (e.g., `/api/auth/login`, `/api/product`), whereas Spring MVC annotations inside `rest/*Controller.java` omit `/api`. Make the base URL/prefix explicit in `api-summary.md` so paths are unambiguous.

2. **Authentication & Refresh Flow:**
   - Stateless Bearer JWT (`JwtAuthenticationFilter` + `SecurityRequirement(name = "bearerAuth")`).
   - Public auth endpoints: `/auth/login` and `/auth/refresh` (`LoginController`). Do **not** document legacy HTTP Basic Auth.

3. **Rate Limiting (`RateLimitingFilter`):**
   - Bucket4j returns `429 Too Many Requests` when tokens are exhausted.
   - Authenticated buckets (per username): `ROLE_USER` (capacity 100, refill 10/min), `ROLE_MANAGER` (capacity 1000, refill 100/min), `ROLE_ADMIN` (capacity 10000, refill 100/min).
   - Unauthenticated bucket (per resolved client IP): check `RateLimitingFilter.newBucket("ip")` for current capacity/refill values.

4. **Standard Error Payload (`ErrorResponse.java`):**
   - Fields serialized by `ErrorResponse` (`@JsonInclude(NON_NULL)`):
     `timestamp`, `status`, `error`, `message`, `path`, and optional `fieldErrors` (`[{ "field": "...", "message": "..." }]`).
   - Verify status codes against `GlobalExceptionHandler.java` (`400` validation/date interval, `401` credentials/expired JWT, `403` access denied, `404` not found, `409` duplicates, `422` `InsufficientQuantityInSaleException` & `BusinessException`).

5. **Language Consistency:**
   - Keep Swagger `@Tag`, `@Operation(summary, description)`, `@Parameter`, `@ApiResponse`, and DTO `@Schema` descriptions in **Portuguese**.
   - Include `@ApiStandardErrors` on authenticated controller methods so `401`, `403`, and `500` appear consistently in Swagger UI.

## Validation Steps

1. Re-run `bash .agents/skills/api-docs-contract-sync/scripts/list-endpoints.sh` and confirm every `@RestController` endpoint is accounted for.
2. Run `./mvnw compile` if any `@Operation`, `@ApiResponses`, or `@Schema` annotations were edited in Java files.
