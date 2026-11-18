---
name: unit-and-security-testing
description: >-
  Use this skill when writing, refactoring, or diagnosing unit tests, security
  filter tests, or RBAC permission checks in LilMarket API. Enforces the strict
  Nested/AAA Mockito conventions, BigDecimal comparison safety, and Bucket4j/JWT
  filter testing patterns.
---

# Unit & Security Testing Runbook

Complement the base rules in `AGENTS.md` with these project-specific testing mechanics and edge cases.

## 1. Service Unit Tests (`src/test/java/.../service/*ServiceImplTest.java`)

Reference: `SupplierServiceImplTest.java`, `SaleServiceImplTest.java`.

- **Class & Group Structure:**
  - `@ExtendWith(MockitoExtension.class)` + `@DisplayName("<Service> Unit Tests")` in English.
  - One `@Nested` class per service method under test, with `@DisplayName` in Portuguese (or matching the existing file's convention) and `@BeforeEach` building clean fixtures via `@Builder`.
  - Test `@DisplayName` in Portuguese: `"Deve <resultado esperado> quando <condição>"`.
- **Strict AAA Blocks & Interaction Hygiene:**
  - Include explicit `//Arrange`, `//Act`, and `//Assert` comments in every `@Test`.
  - Finish assertions by verifying expected mock calls and calling `Mockito.verifyNoMoreInteractions(...)` across all injected mocks so hidden repository writes or missing `StockMovement` calls are immediately caught.
- **Common Pitfalls in LilMarket Tests:**
  - **`BigDecimal` Assertions:** Arithmetic (`multiply`, `subtract`) often produces scale `5` (e.g., `DECIMAL(10,2) * DECIMAL(10,3)`). Use `assertEquals(0, expected.compareTo(actual))` for numeric equality unless explicitly testing `.setScale(2, RoundingMode.HALF_UP)`.
  - **`@Builder.Default` Fields:** Entities like `Customer.credit` use `@Builder.Default`; always rely on `.builder()...build()` rather than `new Entity()` if default values are expected.
  - **Inline MockMaker:** Note `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker` is configured in test resources—compatible with Java records (`*RequestDTO`, `*ResponseDTO`), though instantiating real DTO records in `@BeforeEach` is preferred over mocking records.

## 2. Mandatory Scenario Matrix per Service

Cover all applicable paths for each write/query method:
1. **Happy path:** valid input, entity mapping, persistence, side-effects (e.g., `StockMovementService`, `BatchService`), and mapped `*ResponseDTO`.
2. **Conflict (`409`):** `DuplicateBarcodeException`, `DuplicateBatchCodeException`, `DuplicateSupplierException`, `DuplicateUsernameException`, `DuplicateProductCategoryException`.
3. **Not Found (`404`):** primary entity or referenced FK entity (`Category`, `Supplier`, `Customer`, `User`, `Product`) missing.
4. **Domain Rule / Stock (`422` / `400`):** `InsufficientQuantityInSaleException`, `BusinessException` (e.g., unpaid non-fiado sale, duplicate backfill execution), `InvalidDateIntervalException` (`startDate.isAfter(endDate)`).

## 3. Security, JWT & Rate Limiting Tests

Reference: `src/test/java/com/eduribeiro8/LilMarket/security/ratelimiting/RateLimitingFilterTest.java`.

- **`RateLimitingFilter` (Bucket4j):**
  - Authenticated requests consume from `USER_BUCKET` keyed by username (`ROLE_USER`: capacity 100 / refill 10/min; `ROLE_MANAGER`: capacity 1000 / refill 100/min; `ROLE_ADMIN`: capacity 10000 / refill 100/min).
  - Unauthenticated requests consume from `IP_BUCKET` keyed by `RequestIpResolver.resolveClientIp(request)` (`X-Forwarded-For` first IP -> `X-Real-IP` -> `remoteAddr`).
  - Swagger paths (`/swagger-ui*`, `/v3/api-docs*`, `/webjars*`) bypass filtering via `shouldNotFilter`.
  - Always clear `SecurityContextHolder.clearContext()` in `@AfterEach` when testing filters.
- **RBAC Matrix (`SecurityConfig`):**
  - Public: `/auth/login`, `/auth/refresh`, Swagger UI endpoints.
  - `MANAGER` + `ADMIN`: `POST /customer/*/deposit`, `POST /report/*`.
  - `USER` + `MANAGER` + `ADMIN`: `GET`/`POST`/`PUT` on `/product/**`, `/sale/**`, `/customer/**`, `/batch/**`, `/category/**`, `/supplier/**`, `/restock/**`.
  - `ADMIN` only: `/user/**`, all `DELETE /**`, and any other unmatched route (such as `/admin/**`).

## Validation Commands

- Single method: `./mvnw test -Dtest=ClassName#methodName`
- Single class: `./mvnw test -Dtest=ClassName`
- Full unit suite (`UnitTestSuite` selects package `com.eduribeiro8.LilMarket`): `./mvnw test -Dtest=UnitTestSuite` (avoid `LilMarketApplicationTests` when a live MySQL instance is not running locally).
