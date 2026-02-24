# Agent Guidelines - LilMarket API

This repository contains the LilMarket API, a Spring Boot application for managing a market system (products, stock, sales, etc.).

## 1. Build and Test Commands

The project uses Maven with the Maven Wrapper (`mvnw`).

- **Build project:** `./mvnw clean install`
- **Compile only:** `./mvnw compile`
- **Run application:** `./mvnw spring-boot:run`
- **Run all tests:** `./mvnw test`
- **Run a single test class:** `./mvnw test -Dtest=ClassName`
- **Run a single test method:** `./mvnw test -Dtest=ClassName#methodName`
- **Check for updates:** `./mvnw versions:display-dependency-updates`

## 2. Tech Stack

- **Java:** 17
- **Framework:** Spring Boot 3.1.5
- **Database:** MySQL (using Spring Data JPA)
- **Security:** Spring Security with JWT and Rate Limiting (Bucket4j)
- **Mapping:** MapStruct for DTO-Entity conversion
- **Boilerplate:** Lombok
- **API Documentation:** SpringDoc OpenAPI (Swagger)
- **Validation:** Jakarta Bean Validation (Hibernate Validator)

## 3. Code Style & Conventions

### Naming Conventions
- **Classes:** `PascalCase` (e.g., `ProductController`, `BatchServiceImpl`).
- **Methods & Variables:** `camelCase` (e.g., `findProductById`, `productService`).
- **Packages:** `lowercase.dot.separated` (e.g., `com.eduribeiro8.LilMarket.service`).
- **Constants:** `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_ATTEMPTS`).
- **DTOs:** Suffix with `RequestDTO` or `ResponseDTO` and use Java `record`s.
- **Service Implementation:** Suffix with `ServiceImpl`.

### Project Structure
- `config`: Configuration classes (JPA, Security, OpenAPI, Validation).
- `dto`: Request and Response records for data transfer between layers.
- `entity`: JPA entities representing the database schema.
- `mapper`: MapStruct interfaces for mapping between Entities and DTOs.
- `repository`: Spring Data JPA repositories for database access.
- `rest`: REST controllers defining the API endpoints.
- `rest.exception`: Error handling logic, custom exception classes, and ErrorResponse record.
- `security`: Security filters (JWT, Rate Limiting), configuration, and password encoding.
- `security.logging`: Filters for request/response logging and pre-auth logging.
- `security.ratelimiting`: Rate limiting implementation using Bucket4j.
- `service`: Service interfaces and their corresponding `*ServiceImpl` implementations.

### DTOs and Entities
- **DTOs:** Use Java `record`s for all DTOs. Apply `@Schema` for OpenAPI documentation.
- **Entities:** Use Lombok annotations: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`. Use `@EntityListeners(AuditingEntityListener.class)` for `createdAt` and `updatedAt` fields.
- **Validation:** Use Jakarta annotations (`@NotNull`, `@NotBlank`, `@Size`, `@DecimalMin`, etc.) on both DTOs and Entities.
- **Mapping Example:**
  - DTO: `public record ProductRequestDTO(@NotBlank String name, @NotNull Integer categoryId, ...) {}`
  - Entity: `@Entity public class Product { @Id private Integer id; private String name; ... }`

### Services and Mappers
- Services should be split into an interface and a `*ServiceImpl` class.
- Use `@RequiredArgsConstructor` for constructor-based dependency injection.
- Use `@Transactional` on methods that perform write operations.
- Use MapStruct interfaces in the `mapper` package. Do not write manual mapping logic in services.
- Mapping methods should follow naming: `toEntity`, `toResponse`, `updateEntityFromDTO`.
- Ensure `@Mapper(componentModel = "spring")` is used on mapper interfaces.

### Error Handling
- Centralized in `com.eduribeiro8.LilMarket.rest.exception.GlobalExceptionHandler` using `@RestControllerAdvice`.
- Custom exceptions should extend `RuntimeException` or `BusinessException`.
- Common custom exceptions:
  - `ProductNotFoundException` (404)
  - `DuplicateBarcodeException` (409)
  - `InsufficientQuantityInSaleException` (422)
  - `InvalidDateIntervalException` (400)
- Return `ErrorResponse` for all error scenarios. This record includes `timestamp`, `status`, `error`, `message`, `path`, and optional `errors` (for validation details).

### API Documentation
- Use SpringDoc OpenAPI annotations in Controllers (`@Operation`, `@ApiResponse`, `@Tag`).
- Summary and descriptions are currently in Portuguese. Keep this pattern for consistency in public-facing documentation.
- Use `@ApiStandardErrors` (custom annotation) to include common 401, 403, and 500 error responses.

## 4. Database & Environment
- Primary database is MySQL.
- Use `OffsetDateTime` for timestamp fields in entities for UTC consistency.
- Financial values: Always use `BigDecimal` with `2` decimal places and `RoundingMode.HALF_UP`.
- Configuration is handled via `application.properties` and environment variables.
- Database auditing is enabled via `JpaConfig` and `@EntityListeners(AuditingEntityListener.class)` on entities.
- Audit fields: `createdAt` (updatable = false) and `updatedAt`.

## 5. Security Guidelines
- Authentication is handled via Spring Security with JWT.
- Roles defined in `UserRole`: `ROLE_ADMIN`, `ROLE_MANAGER`, `ROLE_USER`.
- Rate limiting is applied via `RateLimitingFilter` (Bucket4j) per client IP.
- Logging of requests and responses is handled by `LoggingFilter`.
- Do not expose sensitive fields like `password` in `UserResponseDTO`.

## 6. Development Instructions for Agents
- **Stay Idiomatic:** Follow the established pattern of Service/ServiceImpl and Mapper interfaces.
- **Self-Verification:** After making changes, run `./mvnw compile` to ensure no build errors, especially with MapStruct and Lombok processors.
- **MapStruct Generation:** If mapping fails or code seems missing, remember that MapStruct implementations are generated in `target/generated-sources/annotations`.
- **Imports:** Avoid wildcard imports (`import java.util.*`). Organize imports alphabetically.
- **Transactional:** Ensure `@Transactional` is used in service methods that modify multiple repositories or perform complex logic to ensure atomicity.
- **BigDecimal:** Always use `BigDecimal` with a specific scale and `RoundingMode` (e.g., `RoundingMode.HALF_UP`) for financial calculations.
- **Records:** When using DTO records, access fields via `record.field()` rather than `getField()`.
- **Validation:** Always validate incoming requests using `@Valid` in controllers.
- **Testing:**
  - **Unit Tests:** Follow the established pattern for service testing.
    - Use JUnit 5 and Mockito with `@ExtendWith(MockitoExtension.class)`.
    - Group tests using `@Nested` classes for each method being tested.
    - Use `@DisplayName` for classes (English) and methods/nested groups (Portuguese).
    - Strictly follow the **Arrange, Act, Assert (AAA)** pattern with explicit comments in each test.
    - Use `@Mock` for dependencies and `@InjectMocks` for the implementation under test.
    - Use `verifyNoMoreInteractions()` on mocks to ensure no unexpected calls are made.
    - Initialize test data in `@BeforeEach` using `@Builder` for entities.
  - **Integration Tests:** Use `@SpringBootTest`. The project is currently configured for MySQL; consider H2 or Testcontainers if isolation is needed.
