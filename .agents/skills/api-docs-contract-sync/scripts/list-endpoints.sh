#!/usr/bin/env bash
set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../../.." && pwd)"
cd "$REPO_ROOT"

echo "=== Context Path (application.properties) ==="
grep -n "context-path" src/main/resources/application.properties || echo "(default: /)"
echo

echo "=== REST Controllers & Endpoint Mappings ==="
for ctrl in src/main/java/com/eduribeiro8/LilMarket/rest/*Controller.java; do
  echo "--- $(basename "$ctrl") ---"
  grep -nE '@(Request|Get|Post|Put|Delete|Patch)Mapping|@Tag' "$ctrl" || true
  echo
done

echo "=== SecurityConfig Authorization Rules ==="
grep -nE 'requestMatchers|anyRequest|hasRole|hasAnyRole|permitAll' \
  src/main/java/com/eduribeiro8/LilMarket/security/SecurityConfig.java || true
echo

echo "=== GlobalExceptionHandler Status Mappings ==="
grep -nE '@ExceptionHandler|HttpStatus\.[A-Z_]+' \
  src/main/java/com/eduribeiro8/LilMarket/rest/exception/GlobalExceptionHandler.java || true
