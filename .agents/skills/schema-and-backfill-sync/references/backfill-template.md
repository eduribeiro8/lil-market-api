# One-Time Idempotent Backfill Pattern

Reference implementation:
- `src/main/java/com/eduribeiro8/LilMarket/entity/OneTimeJobExecution.java`
- `src/main/java/com/eduribeiro8/LilMarket/service/StockMovementBackfillServiceImpl.java`
- `src/main/java/com/eduribeiro8/LilMarket/rest/StockMovementAdminController.java`

## Core Rules

1. **Unique Job Key:** Define a versioned constant `JOB_NAME` (e.g., `"customer_ledger_backfill_v1"`, max 100 chars).
2. **Double Guard Before Mutating:**
   - Check `oneTimeJobExecutionRepository.existsById(JOB_NAME)` -> throw `BusinessException` (`422`) if already executed.
   - Check target state (e.g., `targetRepository.count() > 0`) when the backfill expects an empty table to prevent duplicate records.
3. **Single `@Transactional` Execution:** Perform all reads, transformations, `saveAll(...)`, and `oneTimeJobExecutionRepository.save(...)` inside one `@Transactional` service method so any failure rolls back both data and job lock.
4. **ADMIN-Only Endpoint:** Expose under `/admin/...` (`POST`), which is automatically restricted to `ROLE_ADMIN` in `SecurityConfig` (`.anyRequest().hasRole("ADMIN")`).

## Service Skeleton

```java
@Service
@RequiredArgsConstructor
public class ExampleBackfillServiceImpl implements ExampleBackfillService {

    private static final String JOB_NAME = "example_backfill_v1";

    private final OneTimeJobExecutionRepository oneTimeJobExecutionRepository;
    // inject target & source repositories

    @Override
    @Transactional
    public ExampleBackfillResponseDTO executeOneTimeBackfill() {
        if (oneTimeJobExecutionRepository.existsById(JOB_NAME)) {
            throw new BusinessException("Backfill já foi executado anteriormente.");
        }

        // 1. Load source entities and compute target records deterministically (UTC timestamps)
        // 2. Persist in batch via targetRepository.saveAll(...)

        OffsetDateTime executedAt = OffsetDateTime.now(ZoneOffset.UTC);
        oneTimeJobExecutionRepository.save(OneTimeJobExecution.builder()
                .jobName(JOB_NAME)
                .executedAt(executedAt)
                .build());

        return new ExampleBackfillResponseDTO(JOB_NAME, executedAt /* , counters */);
    }
}
```
