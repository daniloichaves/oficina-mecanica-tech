#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT_DIR/logs/fase4"

for pid_file in "$LOG_DIR"/*.pid; do
  if [[ -f "$pid_file" ]]; then
    pid="$(cat "$pid_file")"
    if kill -0 "$pid" 2>/dev/null; then
      echo "Encerrando PID $pid"
      kill "$pid" || true
    fi
    rm -f "$pid_file"
  fi
done

echo "Serviços do Fase 4 encerrados."
