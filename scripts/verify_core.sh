#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="/tmp/euclid-javac"

rm -rf "${OUT_DIR}"

java "${ROOT_DIR}/scripts/CompileCheck.java" "${ROOT_DIR}/src/main/java" "${OUT_DIR}"
java -cp "${OUT_DIR}" "${ROOT_DIR}/scripts/RunSmoke.java"

echo "Verification complete."
