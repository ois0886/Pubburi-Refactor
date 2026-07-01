#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

cd "$ROOT_DIR/Server"
./mvnw test

cd "$ROOT_DIR/pubburi-vue"
npm ci
npm run test
npm run build

cd "$ROOT_DIR"
rg -n -i "sk-proj|service_account|private_key|client_email|fcm|api.?key" \
  --glob '!node_modules/**' \
  --glob '!target/**' \
  --glob '!dist/**' \
  --glob '!.git/**' \
  --glob '!scripts/check.sh' \
  . && {
    echo "Sensitive pattern scan found matches. Review before committing."
    exit 1
  }

git ls-files | rg '(^|/)(node_modules|target|dist)(/|$)|(^|/)\.env$|service-account|firebase.*\.json|credentials.*\.json' && {
  echo "Tracked artifact or sensitive file found."
  exit 1
}

echo "Checks passed."
