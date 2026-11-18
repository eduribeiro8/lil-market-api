---
name: git-commit-workflow
description: >-
  Use this skill when preparing, reviewing, or creating git commits or checking
  branch state in LilMarket API. Enforces branch verification, secret/log
  exclusion, pre-commit checks, atomic staging, and the repository's
  Conventional Commits format.
---

# Git Commit & Branch Workflow

Follow this pre-commit runbook before staging or committing changes in LilMarket API.

## 1. Branch & Working Tree Check

1. Run `git branch --show-current` and `git status` to verify you are on the intended branch (e.g., `master` vs a `feat/*` or `fix/*` branch).
2. Inspect `git diff` to separate unrelated pre-existing modifications from the current task. Keep unrelated changes isolated (on their own branch or separate atomic commits).
3. Never stage secrets or runtime artifacts: verify `.env`, `logs/`, `target/`, and credentials are excluded before running `git add`.

## 2. Pre-Commit Verification Gate

Run checks proportional to the files being committed:
- **Java (`src/main/` or `src/test/`):** `./mvnw compile` and `./mvnw test -Dtest=UnitTestSuite` (or targeted `*Test` classes).
- **Docker (`docker-compose.yml` / `Dockerfile`):** `docker compose config -q`.
- **SQL (`lilmarket.sql` / `data_for_tests.sql`):** cross-check against modified `@Entity` classes.
- **Skills (`.agents/skills/`):** confirm valid `name`/`description` YAML frontmatter and relative links.

## 3. Commit Message Convention

Match the repository history (`git log -n 10 --oneline`):

```text
<type>(<scope>): <short imperative summary in lowercase English>
```

- **Types:** `feat`, `fix`, `docs`, `refactor`, `chore`, `config`
- **Common scopes:** `sale`, `product`, `batch`, `stock`, `customer`, `supplier`, `restock`, `report`, `security`, `ratelimiting`, `exception`, `docker`, `agents`, `logs`
- **Formatting rules:**
  - Write the summary in **English**, imperative mood, lowercase start, no trailing period (e.g., `feat(stock): record stock movements on sale and batch operations`).
  - Scope is optional for cross-cutting commits (`docs: update readme`).
  - For complex commits, add a concise bulleted body (`- ...`) explaining what changed and why.
  - Prefer one atomic commit per logical scope rather than bundling unrelated changes.
  - Never run `git push` unless the user explicitly asks.

## Validation Steps

1. Run `git status` and `git log -n 3 --oneline` after committing to confirm clean working tree and expected branch/message.
