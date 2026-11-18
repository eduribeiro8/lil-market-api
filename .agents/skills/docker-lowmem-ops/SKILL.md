---
name: docker-lowmem-ops
description: >-
  Use this skill when modifying Dockerfile, docker-compose.yml, reverse-proxy
  configuration, JVM/MySQL memory flags, logging, or client IP resolution in
  LilMarket API. Preserves 1GB-VPS low-memory budgets and proxy header handling.
---

# Docker Low-Memory & Operations Runbook

LilMarket API is tuned to run on a constrained ~1GB RAM host alongside MySQL 8.0 and a reverse proxy. Keep all infrastructure and operational changes within these boundaries.

## 1. Memory Budget & Runtime Tuning

Inspect `Dockerfile` and `docker-compose.yml` before changing dependencies, caches, thread pools, or container flags:

- **MySQL 8.0 (`lilmarket-db`):**
  - Container limit: `350M`.
  - Required low-memory flags: `--performance_schema=OFF`, `--max_connections=30`, `--innodb_buffer_pool_size=128M`.
  - Avoid raising HikariCP `maximum-pool-size` above MySQL's `max_connections=30` ceiling.
- **Spring Boot API (`lilmarket-api`):**
  - Container limit: `450M`.
  - JVM flags: `-Xmx300m -Xms150m -XX:+UseSerialGC` (leaves ~150MB for metaspace, threads, native buffers, and non-heap memory).
  - Avoid unbounded in-memory caches (`ConcurrentHashMap` growth without bounds, loading entire large tables into memory when `Pageable` or streaming can be used).
  - Image runs as non-root system user `spring:spring` (`eclipse-temurin:17-jre-jammy`).

## 2. Reverse Proxy & Client IP Rate Limiting Chain

Unauthenticated rate limiting in `RateLimitingFilter` depends on accurate client IP resolution:

1. Reverse proxy forwards `X-Forwarded-For` and `X-Real-IP` to `127.0.0.1:8080`.
2. `application.properties` enables `server.forward-headers-strategy=native`.
3. `RequestIpResolver.resolveClientIp(request)` reads the first IP in `X-Forwarded-For`, falls back to `X-Real-IP`, and finally `request.getRemoteAddr()`.

**Warning:** Removing forwarded headers at the proxy or changing `RequestIpResolver` causes all external unauthenticated callers to share the proxy's IP bucket and trigger global `429 Too Many Requests`.

## 3. Logs & Diagnostics

- Rolling application logs are configured via `src/main/resources/logback.xml` (default path `logs/application-YYYY-MM-DD.log`, 30-day history).
- Request/response tracing is handled by `LoggingPreAuthFilter` and `LoggingFilter` (`security/logging/`). Never log raw credentials, `Authorization: Bearer ...` tokens, or `.env` secrets (`DB_PASS`, `JWT_SECRET_KEY`, `MYSQL_ROOT_PASSWORD`).

## Validation Steps

1. Validate compose syntax (without recreating production volumes): `docker compose config -q` (requires `.env` variables set).
2. Verify Java build compatibility with Java 17 target: `./mvnw clean package -DskipTests` or `./mvnw test -Dtest=RateLimitingFilterTest`.
