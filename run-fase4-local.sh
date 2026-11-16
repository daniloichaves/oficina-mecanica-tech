#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT_DIR/logs/fase4"
mkdir -p "$LOG_DIR"

start_service() {
  local name="$1"
  local dir="$2"
  local port="$3"

  if curl -fsS "http://localhost:${port}" >/dev/null 2>&1; then
    echo "[$name] já está em execução na porta ${port}."
    return 0
  fi

  echo "[$name] iniciando em ${dir} (porta ${port})"
  bash -lc "cd '$dir' && nohup mvn spring-boot:run > '$LOG_DIR/${name}.log' 2>&1" &
  echo $! > "$LOG_DIR/${name}.pid"
}

start_service "os-service" "$ROOT_DIR/fase4-priority1/os-service" 8081
start_service "billing-service" "$ROOT_DIR/fase4-priority2/billing-service" 8082
start_service "execution-service" "$ROOT_DIR/fase4-priority3/execution-service" 8083
start_service "saga-orchestrator" "$ROOT_DIR/fase4-priority4/saga-orchestrator" 8084

echo
printf '%s\n' "Serviços do Fase 4 iniciados em segundo plano."
printf '%s\n' "Logs em: $LOG_DIR"
printf '%s\n' "Portas: 8081 (OS), 8082 (Billing), 8083 (Execution), 8084 (Saga)"
printf '%s\n' "Use ./stop-fase4-local.sh para encerrar."
