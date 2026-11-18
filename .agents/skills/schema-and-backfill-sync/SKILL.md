---
name: schema-and-backfill-sync
description: >-
  Use this skill whenever creating or modifying JPA @Entity classes, SQL tables,
  columns, constraints, indexes, or historical data migrations/backfills in
  LilMarket API. Keeps JPA entities, lilmarket.sql, test seeds, and one-time
  backfill jobs synchronized.
---

# Schema Evolution & One-Time Backfill Sync

LilMarket API runs **without Flyway or Liquibase** and relies on explicit SQL schema management (`ddl-auto=none` by default on MySQL 8.0). Every entity change must stay synchronized across Java and SQL artifacts so queries and constraints work reliably at runtime.

## Step-by-Step Workflow

### 1. Synchronize the Three Schema Artifacts
Whenever adding or altering an `@Entity` or `@Column` in `src/main/java/com/eduribeiro8/LilMarket/entity/`:

1. **JPA Entity (`src/main/java/.../entity/*.java`)**
   - Match column names explicitly via `@Column(name = "...")` and table names via `@Table(name = "...")`.
   - Use `OffsetDateTime` (UTC) for timestamps and `@EntityListeners(AuditingEntityListener.class)` for `@CreatedDate` / `@LastModifiedDate`.
   - Match SQL numeric scales:
     - Financial fields: `DECIMAL(10, 2)` (`BigDecimal`, scale `2`).
     - Stock/item quantities: `DECIMAL(10, 3)` (`BigDecimal`, scale `3`).
   - Check enum mapping mode (`@Enumerated(EnumType.STRING)`) against the SQL column (`ENUM(...)` or `VARCHAR(50)`).

2. **Canonical Init Schema (`src/main/resources/lilmarket.sql`)**
   - Mounted into `/docker-entrypoint-initdb.d/init.sql` in `docker-compose.yml` for fresh MySQL volumes.
   - Update `CREATE TABLE`, `FOREIGN KEY`, `UNIQUE KEY`, `INDEX`, and `CHECK` constraints using `ENGINE=InnoDB DEFAULT CHARSET=utf8mb4`.

3. **Test Seed Script (`src/main/resources/data_for_tests.sql`)**
   - Update `INSERT` statements if `NOT NULL` columns without defaults were added or renamed.

### 2. Provide Incremental DDL for Existing Volumes
Because `/docker-entrypoint-initdb.d/init.sql` only executes when `mysql_data` is empty (and Hibernate does not auto-alter tables), existing MySQL volumes must be updated manually to avoid runtime SQL errors.
- Always provide an idempotent or ready-to-run `ALTER TABLE ...` / `CREATE TABLE IF NOT EXISTS ...` SQL snippet in your response or migration notes when modifying existing schema structures.

### 3. Historical Data Backfills (One-Time Jobs)
When a new table or derived column requires computing historical data from existing `sales`, `sale_items`, `batches`, or `restock` records:
- Follow the idempotent one-shot job pattern tracked in `one_time_job_execution` (`OneTimeJobExecution` entity + `OneTimeJobExecutionRepository`).
- Read [backfill-template.md](./references/backfill-template.md) for the exact implementation pattern based on `StockMovementBackfillServiceImpl` and `StockMovementAdminController`.

## Validation Steps

1. Run `./mvnw compile` to verify JPA/Lombok annotations compile cleanly.
2. Cross-check every `@Column`, `@JoinColumn`, `@Id`, and `@Enumerated` field in the modified entity against `src/main/resources/lilmarket.sql` line by line (column name, nullability, scale, and constraints).
3. Run `./mvnw test` to ensure unit tests pass with any new entity fields (`@Builder.Default` where defaults are expected in memory).
