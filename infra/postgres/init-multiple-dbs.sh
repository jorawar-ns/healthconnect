#!/bin/bash
# Creates one database per service from POSTGRES_MULTIPLE_DATABASES env var.
# Called automatically by the postgres docker-entrypoint.

set -e

function create_db() {
  local db=$1
  echo "Creating database: $db"
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE "$db";
    GRANT ALL PRIVILEGES ON DATABASE "$db" TO "$POSTGRES_USER";
EOSQL
}

if [ -n "$POSTGRES_MULTIPLE_DATABASES" ]; then
  for db in $(echo "$POSTGRES_MULTIPLE_DATABASES" | tr ',' ' '); do
    trimmed=$(echo "$db" | xargs)   # strip whitespace
    create_db "$trimmed"
  done
fi
