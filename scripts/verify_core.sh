#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
OUT_DIR="/tmp/euclid-smoke"
CP_FILE="/tmp/euclid-classpath.txt"

rm -rf "${OUT_DIR}"
mkdir -p "${OUT_DIR}"

pushd "${ROOT_DIR}" >/dev/null
mvn -q -DskipTests compile dependency:build-classpath -Dmdep.outputFile="${CP_FILE}" -Dmdep.pathSeparator=:
javac -cp "target/classes:$(cat "${CP_FILE}")" -d "${OUT_DIR}" scripts/RunSmoke.java
java -cp "${OUT_DIR}:target/classes:$(cat "${CP_FILE}")" RunSmoke
popd >/dev/null

echo "Verification complete."
