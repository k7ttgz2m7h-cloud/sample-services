#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_DIR="$ROOT_DIR/logs"
PID_DIR="$ROOT_DIR/pids"

mkdir -p "$LOG_DIR" "$PID_DIR"

services=(
  order-service
  payment-service
  customer-service
  inventory-service
  pricing-service
  fraud-service
  fulfillment-service
  shipping-service
  notification-service
)

for service in "${services[@]}"; do
  pid_file="$PID_DIR/$service.pid"
  log_file="$LOG_DIR/$service.log"

  if [[ -f "$pid_file" ]] && kill -0 "$(cat "$pid_file")" 2>/dev/null; then
    echo "$service already running with pid $(cat "$pid_file")"
    continue
  fi

  echo "Starting $service..."
  (
    cd "$ROOT_DIR"
    ../mvnw -q -pl "$service" spring-boot:run >"$log_file" 2>&1
  ) &
  echo $! >"$pid_file"
done

echo
echo "Started sample services. Logs: $LOG_DIR"
echo "Check health with: curl http://localhost:8081/actuator/health"
